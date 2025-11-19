package com.zyn.aiclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI模型信息DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiModelInfo {

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 模型版本
     */
    private String version;

    /**
     * 模型描述
     */
    private String description;

    /**
     * 准确率
     */
    private Double accuracy;

    /**
     * 支持的视频格式
     */
    private String[] supportedFormats;

    /**
     * 最大视频时长（秒）
     */
    private Integer maxDurationSeconds;

    /**
     * 模型加载时间
     */
    private LocalDateTime loadedAt;

    /**
     * 是否可用
     */
    @Builder.Default
    private Boolean available = true;
}
