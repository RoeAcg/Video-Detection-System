package com.zyn.common.entity;

import com.zyn.common.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

/**
 * 检测任务实体类
 */
@Entity
@Table(name = "detection_tasks", indexes = {
        @Index(name = "idx_task_id", columnList = "task_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_video_id", columnList = "video_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetectionTask extends BaseEntity {

    @Column(name = "task_id", nullable = false, unique = true, length = 36)
    private String taskId;  // UUID

    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private TaskStatus status = TaskStatus.PENDING;

    @Column(name = "progress")
    @Builder.Default
    private Integer progress = 0;  // 0-100

    @Column(name = "estimated_time_seconds")
    private Integer estimatedTimeSeconds;

    @Column(name = "started_at")
    private java.time.LocalDateTime startedAt;

    @Column(name = "completed_at")
    private java.time.LocalDateTime completedAt;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;
    
    @Column(name = "mode", length = 20)
    @Builder.Default
    private String mode = "standard";  // 检测模式: standard 或 aigc
}
