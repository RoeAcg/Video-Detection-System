package com.zyn.aiclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI检测请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiDetectionRequest {

    /**
     * 视频文件路径或URL
     */
    private String videoPath;

    /**
     * 视频文件哈希（用于去重）
     */
    private String fileHash;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 检测模式: fast(快速), standard(标准), thorough(深度)
     */
    @Builder.Default
    private String mode = "standard";

    /**
     * 采样帧率 (每秒提取多少帧)
     */
    @Builder.Default
    private Integer frameRate = 5;

    /**
     * 最大处理帧数
     */
    @Builder.Default
    private Integer maxFrames = 300;

    /**
     * 是否返回特征向量
     */
    @Builder.Default
    private Boolean includeFeatures = true;

    /**
     * 超时时间（秒）
     */
    @Builder.Default
    private Integer timeoutSeconds = 120;
}
