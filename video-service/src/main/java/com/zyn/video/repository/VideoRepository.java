package com.zyn.video.repository;

import com.zyn.common.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 视频仓库
 */
@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    /**
     * 根据文件哈希查找视频（去重）
     */
    Optional<Video> findByFileHash(String fileHash);

    /**
     * 根据用户ID查找视频列表
     */
    Page<Video> findByUserId(Long userId, Pageable pageable);

    /**
     * 检查文件哈希是否存在
     */
    boolean existsByFileHash(String fileHash);
}
