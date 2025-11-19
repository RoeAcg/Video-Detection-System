package com.zyn.common.entity;

import com.zyn.common.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.*;

/**
 * 用户举报实体类
 */
@Entity
@Table(name = "reports", indexes = {
        @Index(name = "idx_detection_id", columnList = "detection_id"),
        @Index(name = "idx_reporter_id", columnList = "reporter_id"),
        @Index(name = "idx_status", columnList = "status")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report extends BaseEntity {

    @Column(name = "detection_id", nullable = false)
    private Long detectionId;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @Column(name = "reason", length = 50)
    private String reason;

    @Column(name = "evidence", length = 2000)
    private String evidence;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ReportStatus status = ReportStatus.SUBMITTED;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "reviewed_at")
    private java.time.LocalDateTime reviewedAt;

    @Column(name = "review_notes", length = 1000)
    private String reviewNotes;
}
