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
     * 任务唯一标识 ID (后端生成)
     */
    private String taskId;

    /**
     * 待检测文件的绝对路径 (支持视频和图片)
     */
    private String videoPath;

    /**
     * 检测模式: standard(人脸伪造检测), aigc(通用生成检测)
     */
    @Builder.Default
    private String mode = "standard";

    /**
     * 文件哈希 (用于校验)
     */
    @Builder.Default
    private String fileHash = "";

    /**
     * 视频抽帧频率 (每秒几帧)
     */
    @Builder.Default
    private Integer frameRate = 5;

    /**
     * 最大检测帧数
     */
    @Builder.Default
    private Integer maxFrames = 300;
}
