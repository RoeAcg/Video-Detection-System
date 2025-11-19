package com.zyn.worker.repository;

import com.zyn.common.entity.DetectionTask;
import com.zyn.common.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 检测任务仓库
 */
@Repository
public interface DetectionTaskRepository extends JpaRepository<DetectionTask, Long> {

    /**
     * 根据任务ID查找任务
     */
    Optional<DetectionTask> findByTaskId(String taskId);

    /**
     * 根据用户ID和状态查找任务
     */
    List<DetectionTask> findByUserIdAndStatus(Long userId, TaskStatus status);

    /**
     * 查找超时的任务
     */
    List<DetectionTask> findByStatusAndStartedAtBefore(TaskStatus status, LocalDateTime time);
}