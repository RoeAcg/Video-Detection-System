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
     * 对应的任务 ID
     */
    private String taskId;

    /**
     * 最终判定结果: FAKE (伪造) 或 REAL (真实)
     */
    private String result;

    /**
     * 置信度 (0.0 - 1.0)，越高越可信
     */
    private BigDecimal confidence;

    /**
     * 使用的模型名称 (effort 或 drct)
     */
    private String modelVersion;

    /**
     * 处理耗时 (毫秒)
     */
    private Long processingTimeMs;

    /**
     * 分析的帧数 (API文档虽未明确列在表格，但示例响应中有)
     */
    private Integer framesAnalyzed;

    /**
     * 多维度特征分数
     */
    private Map<String, Object> features;

    /**
     * 检测到的伪造痕迹列表
     */
    private List<String> artifacts;

    /**
     * 错误信息 (如果失败)
     */
    private String error;
}
