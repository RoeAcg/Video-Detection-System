package com.zyn.video.service.impl;

import com.zyn.common.constant.AppConstants;
import com.zyn.common.constant.KafkaTopics;
import com.zyn.common.dto.event.DetectionTaskEvent;
import com.zyn.common.dto.response.PageResponse;
import com.zyn.common.dto.response.VideoUploadResponse;
import com.zyn.common.entity.ChunkMetadata;
import com.zyn.common.entity.DetectionTask;
import com.zyn.common.entity.Video;
import com.zyn.common.enums.TaskStatus;
import com.zyn.common.exception.BusinessException;
import com.zyn.common.exception.FileUploadException;
import com.zyn.common.exception.ForbiddenException;
import com.zyn.common.exception.ResourceNotFoundException;
import com.zyn.common.util.FileUtil;
import com.zyn.common.util.HashUtil;
import com.zyn.video.repository.ChunkMetadataRepository;
import com.zyn.video.repository.VideoRepository;
import com.zyn.video.service.FileStorageService;
import com.zyn.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 视频服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final ChunkMetadataRepository chunkMetadataRepository;
    private final FileStorageService fileStorageService;
    private final KafkaTemplate<String, DetectionTaskEvent> kafkaTemplate;

    @Override
    @Transactional
    public VideoUploadResponse uploadVideo(MultipartFile file, String description, Long userId, String mode) {
        // 验证文件
        validateVideoFile(file);

        // 计算文件哈希
        String fileHash = calculateFileHash(file);

        // 检查去重
        Video existingVideo = videoRepository.findByFileHash(fileHash).orElse(null);
        if (existingVideo != null) {
            log.info("视频已存在（去重）: {}", fileHash);
            return createUploadResponse(existingVideo);
        }

        // 存储文件
        String filePath = fileStorageService.storeFile(file, userId);

        // 保存视频元数据
        Video video = Video.builder()
                .userId(userId)
                .fileName(file.getOriginalFilename())
                .fileHash(fileHash)
                .filePath(filePath)
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .description(description)
                .build();

        video = videoRepository.save(video);
        log.info("视频保存成功: videoId={}, hash={}", video.getId(), fileHash);

        // 创建检测任务
        String taskId = createDetectionTask(video, userId, mode);

        // 发送Kafka消息
        sendDetectionTaskEvent(video, taskId, userId, mode);

        return VideoUploadResponse.builder()
                .taskId(taskId)
                .uploadStatus("SUCCESS")
                .uploadProgress(100)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public String initChunkUpload(String fileName, Long fileSize, Integer totalChunks, Long userId) {
        // 验证参数
        if (fileSize > AppConstants.MAX_FILE_SIZE) {
            throw FileUploadException.fileTooLarge(AppConstants.MAX_FILE_SIZE);
        }

        if (totalChunks > AppConstants.MAX_CHUNKS) {
            throw new FileUploadException("分块数量超过限制: " + AppConstants.MAX_CHUNKS);
        }

        // 生成文件ID
        String fileId = UUID.randomUUID().toString();
        log.info("初始化分块上传: fileId={}, fileName={}, totalChunks={}",
                fileId, fileName, totalChunks);

        return fileId;
    }

    @Override
    @Transactional
    public void uploadChunk(String fileId, Integer chunkIndex, MultipartFile chunk) {
        // 计算分块哈希
        String chunkHash = calculateFileHash(chunk);

        // 检查分块是否已上传
        if (chunkMetadataRepository.existsByFileIdAndChunkIndex(fileId, chunkIndex)) {
            log.warn("分块已存在: fileId={}, chunkIndex={}", fileId, chunkIndex);
            return;
        }

        // 存储分块
        String chunkPath = fileStorageService.storeChunk(fileId, chunkIndex, chunk);

        // 保存分块元数据
        ChunkMetadata metadata = ChunkMetadata.builder()
                .fileId(fileId)
                .chunkIndex(chunkIndex)
                .chunkHash(chunkHash)
                .chunkPath(chunkPath)
                .chunkSize(chunk.getSize())
                .uploadedAt(LocalDateTime.now())
                .verified(true)
                .build();

        chunkMetadataRepository.save(metadata);
        log.debug("分块保存成功: fileId={}, chunkIndex={}", fileId, chunkIndex);
    }

    @Override
    @Transactional
    public VideoUploadResponse completeChunkUpload(String fileId, String description, Long userId) {
        // 获取所有分块
        List<ChunkMetadata> chunks = chunkMetadataRepository.findByFileIdOrderByChunkIndexAsc(fileId);

        if (chunks.isEmpty()) {
            throw new FileUploadException("未找到分块数据");
        }

        // 合并分块
        String mergedFilePath = fileStorageService.mergeChunks(fileId, chunks);

        // 计算完整文件哈希
        String fileHash = fileStorageService.calculateMergedFileHash(mergedFilePath);

        // 检查去重
        Video existingVideo = videoRepository.findByFileHash(fileHash).orElse(null);
        if (existingVideo != null) {
            log.info("视频已存在（去重）: {}", fileHash);
            fileStorageService.deleteFile(mergedFilePath);
            return createUploadResponse(existingVideo);
        }

        // 保存视频元数据
        long totalSize = chunks.stream().mapToLong(ChunkMetadata::getChunkSize).sum();
        String fileName = "merged_" + fileId + ".mp4";

        Video video = Video.builder()
                .userId(userId)
                .fileName(fileName)
                .fileHash(fileHash)
                .filePath(mergedFilePath)
                .fileSize(totalSize)
                .description(description)
                .build();

        video = videoRepository.save(video);
        log.info("分块视频合并完成: videoId={}, hash={}", video.getId(), fileHash);

        // 清理分块数据
        fileStorageService.cleanupChunks(fileId);
        chunkMetadataRepository.deleteByFileId(fileId);

        // 创建检测任务（分块上传默认使用standard模式）
        String taskId = createDetectionTask(video, userId, "standard");

        // 发送Kafka消息
        sendDetectionTaskEvent(video, taskId, userId, "standard");

        return VideoUploadResponse.builder()
                .taskId(taskId)
                .uploadStatus("SUCCESS")
                .uploadProgress(100)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    public Video getVideo(Long videoId, Long userId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("视频", videoId));

        // 权限检查
        if (!video.getUserId().equals(userId)) {
            throw ForbiddenException.resourceAccessDenied("视频");
        }

        return video;
    }

    @Override
    public PageResponse<Video> getUserVideos(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Video> videoPage = videoRepository.findByUserId(userId, pageable);

        return PageResponse.<Video>builder()
                .content(videoPage.getContent())
                .page(page)
                .size(size)
                .totalPages(videoPage.getTotalPages())
                .totalElements(videoPage.getTotalElements())
                .first(videoPage.isFirst())
                .last(videoPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public void deleteVideo(Long videoId, Long userId) {
        Video video = getVideo(videoId, userId);

        // 删除文件
        fileStorageService.deleteFile(video.getFilePath());

        // 删除数据库记录
        videoRepository.delete(video);

        log.info("视频删除成功: videoId={}", videoId);
    }

    /**
     * 验证媒体文件（视频或图片）
     */
    private void validateVideoFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileUploadException("文件为空");
        }

        if (!FileUtil.isValidMediaFormat(file.getOriginalFilename())) {
            throw FileUploadException.invalidFileType("mp4, avi, mov, mkv, webm, jpg, jpeg, png, bmp, gif");
        }

        if (!FileUtil.isValidFileSize(file.getSize(), AppConstants.MAX_FILE_SIZE)) {
            throw FileUploadException.fileTooLarge(AppConstants.MAX_FILE_SIZE);
        }
    }

    /**
     * 计算文件哈希
     */
    private String calculateFileHash(MultipartFile file) {
        try {
            return HashUtil.sha256(file.getInputStream());
        } catch (Exception e) {
            throw new FileUploadException("计算文件哈希失败", e);
        }
    }

    /**
     * 创建检测任务
     */
    private String createDetectionTask(Video video, Long userId, String mode) {
        String taskId = UUID.randomUUID().toString();

        DetectionTask task = DetectionTask.builder()
                .taskId(taskId)
                .videoId(video.getId())
                .userId(userId)
                .status(TaskStatus.PENDING)
                .progress(0)
                .build();

        // 这里应该保存到 DetectionTask 表，但为了简化，我们直接返回taskId
        log.info("创建检测任务: taskId={}, videoId={}", taskId, video.getId());

        return taskId;
    }

    /**
     * 发送检测任务事件到Kafka
     */
    private void sendDetectionTaskEvent(Video video, String taskId, Long userId, String mode) {
        DetectionTaskEvent event = DetectionTaskEvent.builder()
                .taskId(taskId)
                .videoId(video.getId())
                .userId(userId)
                .videoPath(video.getFilePath())
                .fileHash(video.getFileHash())
                .mode(mode)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(KafkaTopics.DETECTION_TASKS, taskId, event);
        log.info("发送检测任务到Kafka: taskId={}", taskId);
    }

    /**
     * 创建上传响应
     */
    private VideoUploadResponse createUploadResponse(Video video) {
        return VideoUploadResponse.builder()
                .taskId("DUPLICATE")
                .uploadStatus("DUPLICATE")
                .uploadProgress(100)
                .createdAt(video.getCreatedAt())
                .build();
    }
}
