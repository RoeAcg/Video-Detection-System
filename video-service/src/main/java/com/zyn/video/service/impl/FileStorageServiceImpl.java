package com.zyn.video.service.impl;

import com.zyn.common.entity.ChunkMetadata;
import com.zyn.common.exception.FileUploadException;
import com.zyn.common.util.FileUtil;
import com.zyn.common.util.HashUtil;
import com.zyn.video.config.FileStorageConfig;
import com.zyn.video.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * 文件存储服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final FileStorageConfig storageConfig;

    @Override
    public String storeFile(MultipartFile file, Long userId) {
        try {
            // 创建用户目录
            String userDir = storageConfig.getUploadDir() + File.separator + userId;
            FileUtil.createDirectoryIfNotExists(userDir);

            // 生成唯一文件名
            String uniqueFileName = FileUtil.generateUniqueFileName(file.getOriginalFilename());
            String filePath = userDir + File.separator + uniqueFileName;

            // 保存文件
            Path targetPath = Paths.get(filePath);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("文件存储成功: {}", targetPath.toAbsolutePath());
            return targetPath.toAbsolutePath().toString();

        } catch (IOException e) {
            throw new FileUploadException("文件存储失败", e);
        }
    }

    @Override
    public String storeChunk(String fileId, Integer chunkIndex, MultipartFile chunk) {
        try {
            // 创建分块目录
            String chunkDir = storageConfig.getChunkDir() + File.separator + fileId;
            FileUtil.createDirectoryIfNotExists(chunkDir);

            // 保存分块
            String chunkFileName = String.format("chunk_%04d", chunkIndex);
            String chunkPath = chunkDir + File.separator + chunkFileName;

            Path targetPath = Paths.get(chunkPath);
            Files.copy(chunk.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            log.debug("分块存储成功: {}", chunkPath);
            return chunkPath;

        } catch (IOException e) {
            throw new FileUploadException("分块存储失败", e);
        }
    }

    @Override
    public String mergeChunks(String fileId, List<ChunkMetadata> chunks) {
        try {
            // 创建合并后的文件路径
            String mergedDir = storageConfig.getUploadDir() + File.separator + "merged";
            FileUtil.createDirectoryIfNotExists(mergedDir);

            String mergedFileName = fileId + ".mp4";
            String mergedFilePath = mergedDir + File.separator + mergedFileName;

            // 合并分块
            try (FileOutputStream fos = new FileOutputStream(mergedFilePath);
                 BufferedOutputStream bos = new BufferedOutputStream(fos)) {

                for (ChunkMetadata chunk : chunks) {
                    try (FileInputStream fis = new FileInputStream(chunk.getChunkPath());
                         BufferedInputStream bis = new BufferedInputStream(fis)) {

                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = bis.read(buffer)) != -1) {
                            bos.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }

            String absolutePath = Paths.get(mergedFilePath).toAbsolutePath().toString();
            log.info("分块合并成功: {}, 总分块数: {}", absolutePath, chunks.size());
            return absolutePath;

        } catch (IOException e) {
            throw FileUploadException.chunkMergeFailure();
        }
    }

    @Override
    public String calculateMergedFileHash(String filePath) {
        try {
            File file = new File(filePath);
            return HashUtil.sha256(file);
        } catch (Exception e) {
            throw new FileUploadException("计算文件哈希失败", e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        if (FileUtil.fileExists(filePath)) {
            FileUtil.deleteFile(filePath);
            log.info("文件删除成功: {}", filePath);
        }
    }

    @Override
    public void cleanupChunks(String fileId) {
        String chunkDir = storageConfig.getChunkDir() + File.separator + fileId;
        FileUtil.deleteDirectory(chunkDir);
        log.info("分块清理成功: {}", chunkDir);
    }
}
