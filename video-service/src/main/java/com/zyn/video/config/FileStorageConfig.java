package com.zyn.video.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件存储配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file.storage")
public class FileStorageConfig {

    /**
     * 文件上传目录
     */
    private String uploadDir = "./uploads";

    /**
     * 分块临时目录
     */
    private String chunkDir = "./chunks";

    /**
     * 最大文件大小（字节）
     */
    private Long maxFileSize = 2L * 1024 * 1024 * 1024;  // 2GB

    /**
     * 允许的文件类型
     */
    private String[] allowedTypes = {"mp4", "avi", "mov", "mkv", "webm"};
}
