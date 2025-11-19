package com.zyn.common.dto.response;

import com.zyn.common.enums.DetectionResultEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 检测结果响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetectionResponse {

    private Long detectionId;

    private Long videoId;

    private String fileName;

    private DetectionResultEnum result;

    private BigDecimal confidence;

    private String modelVersion;

    private Long processingTimeMs;

    private Integer framesAnalyzed;

    private List<String> artifacts;

    private Map<String, Object> features;

    private LocalDateTime createdAt;
}
