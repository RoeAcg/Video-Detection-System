package com.zyn.video.service;

import com.zyn.common.entity.ChunkMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件存储服务接口
 */
public interface FileStorageService {

    /**
     * 存储文件
     */
    String storeFile(MultipartFile file, Long userId);

    /**
     * 存储分块
     */
    String storeChunk(String fileId, Integer chunkIndex, MultipartFile chunk);

    /**
     * 合并分块
     */
    String mergeChunks(String fileId, List<ChunkMetadata> chunks);

    /**
     * 计算合并后文件的哈希
     */
    String calculateMergedFileHash(String filePath);

    /**
     * 删除文件
     */
    void deleteFile(String filePath);

    /**
     * 清理分块
     */
    void cleanupChunks(String fileId);
}
