package com.zyn.common.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 分块上传元数据实体类
 */
@Entity
@Table(name = "chunk_metadata",
        uniqueConstraints = @UniqueConstraint(columnNames = {"file_id", "chunk_index"}),
        indexes = {
                @Index(name = "idx_file_id", columnList = "file_id")
        }
)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChunkMetadata extends BaseEntity {

    @Column(name = "file_id", nullable = false, length = 36)
    private String fileId;  // 文件标识UUID

    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    @Column(name = "chunk_hash", length = 64)
    private String chunkHash;  // SHA-256

    @Column(name = "chunk_path", length = 500)
    private String chunkPath;

    @Column(name = "chunk_size")
    private Long chunkSize;

    @Column(name = "uploaded_at")
    private java.time.LocalDateTime uploadedAt;

    @Column(name = "verified")
    @Builder.Default
    private Boolean verified = false;
}
