package com.zyn.aiclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * AI检测响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiDetectionResponse {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 检测结果: AUTHENTIC(真实), FAKE(伪造), UNCERTAIN(不确定)
     */
    private String result;

    /**
     * 置信度 (0.0 - 1.0)
     */
    private BigDecimal confidence;

    /**
     * 模型版本
     */
    private String modelVersion;

    /**
     * 处理时间（毫秒）
     */
    private Long processingTimeMs;

    /**
     * 分析的帧数
     */
    private Integer framesAnalyzed;

    /**
     * 检测到的伪造迹象列表
     */
    private List<String> artifacts;

    /**
     * 特征向量（可选）
     */
    private Map<String, Object> features;

    /**
     * 每帧置信度分数
     */
    private List<Double> frameScores;

    /**
     * 错误消息（如果失败）
     */
    private String errorMessage;

    /**
     * 是否成功
     */
    @Builder.Default
    private Boolean success = true;
}
