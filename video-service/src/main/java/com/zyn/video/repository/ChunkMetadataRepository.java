package com.zyn.video.repository;

import com.zyn.common.entity.ChunkMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 分块元数据仓库
 */
@Repository
public interface ChunkMetadataRepository extends JpaRepository<ChunkMetadata, Long> {

    /**
     * 根据文件ID查找所有分块（按索引排序）
     */
    List<ChunkMetadata> findByFileIdOrderByChunkIndexAsc(String fileId);

    /**
     * 检查分块是否存在
     */
    boolean existsByFileIdAndChunkIndex(String fileId, Integer chunkIndex);

    /**
     * 删除文件的所有分块
     */
    void deleteByFileId(String fileId);
}
