package com.zyn.video.controller;

import com.zyn.common.constant.AppConstants;
import com.zyn.common.dto.response.PageResponse;
import com.zyn.common.dto.response.VideoUploadResponse;
import com.zyn.common.entity.Video;
import com.zyn.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    /**
     * 上传视频（小文件直接上传）
     */
    @PostMapping("/upload")
    public ResponseEntity<VideoUploadResponse> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description,
            Authentication authentication) {

        log.info("接收到视频上传请求: {}, 大小: {} bytes",
                file.getOriginalFilename(), file.getSize());

        Long userId = getUserIdFromAuth(authentication);
        VideoUploadResponse response = videoService.uploadVideo(file, description, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 分块上传 - 初始化
     */
    @PostMapping("/upload/init")
    public ResponseEntity<String> initChunkUpload(
            @RequestParam("fileName") String fileName,
            @RequestParam("fileSize") Long fileSize,
            @RequestParam("totalChunks") Integer totalChunks,
            Authentication authentication) {

        log.info("初始化分块上传: {}, 大小: {} bytes, 分块数: {}",
                fileName, fileSize, totalChunks);

        Long userId = getUserIdFromAuth(authentication);
        String fileId = videoService.initChunkUpload(fileName, fileSize, totalChunks, userId);

        return ResponseEntity.ok(fileId);
    }

    /**
     * 分块上传 - 上传分块
     */
    @PostMapping("/upload/chunk")
    public ResponseEntity<String> uploadChunk(
            @RequestParam("fileId") String fileId,
            @RequestParam("chunkIndex") Integer chunkIndex,
            @RequestParam("file") MultipartFile chunk,
            Authentication authentication) {

        log.debug("上传分块: fileId={}, chunkIndex={}, size={}",
                fileId, chunkIndex, chunk.getSize());

        videoService.uploadChunk(fileId, chunkIndex, chunk);

        return ResponseEntity.ok("分块上传成功");
    }

    /**
     * 分块上传 - 完成上传
     */
    @PostMapping("/upload/complete")
    public ResponseEntity<VideoUploadResponse> completeChunkUpload(
            @RequestParam("fileId") String fileId,
            @RequestParam(value = "description", required = false) String description,
            Authentication authentication) {

        log.info("完成分块上传: fileId={}", fileId);

        Long userId = getUserIdFromAuth(authentication);
        VideoUploadResponse response = videoService.completeChunkUpload(fileId, description, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取视频详情
     */
    @GetMapping("/{videoId}")
    public ResponseEntity<Video> getVideo(@PathVariable Long videoId,
                                          Authentication authentication) {
        log.info("获取视频详情: {}", videoId);

        Long userId = getUserIdFromAuth(authentication);
        Video video = videoService.getVideo(videoId, userId);

        return ResponseEntity.ok(video);
    }

    /**
     * 获取用户的视频列表
     */
    @GetMapping("/my")
    public ResponseEntity<PageResponse<Video>> getMyVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        log.info("获取用户视频列表: userId={}, page={}, size={}", userId, page, size);

        PageResponse<Video> response = videoService.getUserVideos(userId, page, size);

        return ResponseEntity.ok(response);
    }

    /**
     * 删除视频
     */
    @DeleteMapping("/{videoId}")
    public ResponseEntity<String> deleteVideo(@PathVariable Long videoId,
                                              Authentication authentication) {
        log.info("删除视频: {}", videoId);

        Long userId = getUserIdFromAuth(authentication);
        videoService.deleteVideo(videoId, userId);

        return ResponseEntity.ok("视频删除成功");
    }

    /**
     * 从认证信息中获取用户ID
     */
    private Long getUserIdFromAuth(Authentication authentication) {
        // 这里简化处理，实际应该从JWT中解析
        return 1L; // TODO: 从JWT token中获取真实用户ID
    }
}
