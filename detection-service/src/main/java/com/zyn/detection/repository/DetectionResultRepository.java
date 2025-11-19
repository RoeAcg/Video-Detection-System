package com.zyn.detection.repository;

import com.zyn.common.entity.DetectionResult;
import com.zyn.common.enums.DetectionResultEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 检测结果仓库
 */
@Repository
public interface DetectionResultRepository extends JpaRepository<DetectionResult, Long> {

    Optional<DetectionResult> findByTaskId(String taskId);

    Optional<DetectionResult> findByVideoId(Long videoId);

    Page<DetectionResult> findByUserId(Long userId, Pageable pageable);

    Page<DetectionResult> findByUserIdAndPrediction(
            Long userId, DetectionResultEnum prediction, Pageable pageable);

    long countByUserId(Long userId);

    long countByUserIdAndPrediction(Long userId, DetectionResultEnum prediction);

    @Query("SELECT AVG(d.confidence) FROM DetectionResult d WHERE d.userId = :userId")
    Double calculateAverageConfidence(@Param("userId") Long userId);
}
