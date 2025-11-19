package com.zyn.common.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 视频实体类
 */
@Entity
@Table(name = "videos", indexes = {
        @Index(name = "idx_file_hash", columnList = "file_hash"),
        @Index(name = "idx_user_id", columnList = "user_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_hash", nullable = false, unique = true, length = 64)
    private String fileHash;  // SHA-256哈希，用于去重

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "description", length = 500)
    private String description;
}

