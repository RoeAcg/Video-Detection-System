package com.zyn.worker.repository;

import com.zyn.common.entity.DetectionResult;
import com.zyn.common.enums.DetectionResultEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 检测结果仓库
 */
@Repository
public interface DetectionResultRepository extends JpaRepository<DetectionResult, Long> {

    /**
     * 根据任务ID查找结果
     */
    Optional<DetectionResult> findByTaskId(String taskId);

    /**
     * 根据视频ID查找结果
     */
    Optional<DetectionResult> findByVideoId(Long videoId);

    /**
     * 根据用户ID查找结果列表
     */
    Page<DetectionResult> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据用户ID和预测结果查找
     */
    List<DetectionResult> findByUserIdAndPrediction(Long userId, DetectionResultEnum prediction);
}
