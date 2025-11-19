package com.zyn.detection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统计响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsResponse {

    /**
     * 总检测数
     */
    private Long totalDetections;

    /**
     * 真实视频数
     */
    private Long authenticCount;

    /**
     * 伪造视频数
     */
    private Long fakeCount;

    /**
     * 不确定数
     */
    private Long uncertainCount;

    /**
     * 平均置信度
     */
    private Double averageConfidence;
}
