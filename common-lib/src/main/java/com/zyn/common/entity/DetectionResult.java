package com.zyn.common.entity;

import com.zyn.common.enums.DetectionResultEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 检测结果实体类
 */
@Entity
@Table(name = "detection_results", indexes = {
        @Index(name = "idx_task_id", columnList = "task_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_video_id", columnList = "video_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetectionResult extends BaseEntity {

    @Column(name = "task_id", nullable = false, length = 36)
    private String taskId;

    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "prediction", nullable = false, length = 20)
    private DetectionResultEnum prediction;  // AUTHENTIC, FAKE, UNCERTAIN

    @Column(name = "confidence", precision = 5, scale = 4)
    private BigDecimal confidence;  // 0.0000 - 1.0000

    @Column(name = "model_version", length = 50)
    private String modelVersion;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    @Column(name = "frames_analyzed")
    private Integer framesAnalyzed;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "features", columnDefinition = "jsonb")
    private Map<String, Object> features;

    @Column(name = "artifacts_detected", length = 1000)
    private String artifactsDetected;  // 逗号分隔的伪造迹象列表
}