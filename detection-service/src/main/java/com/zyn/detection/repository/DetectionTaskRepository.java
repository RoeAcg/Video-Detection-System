package com.zyn.detection.repository;

import com.zyn.common.entity.DetectionTask;
import com.zyn.common.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 检测任务仓库
 */
@Repository
public interface DetectionTaskRepository extends JpaRepository<DetectionTask, Long> {

    Optional<DetectionTask> findByTaskId(String taskId);

    Page<DetectionTask> findByUserId(Long userId, Pageable pageable);

    Page<DetectionTask> findByUserIdAndStatus(Long userId, TaskStatus status, Pageable pageable);
}
