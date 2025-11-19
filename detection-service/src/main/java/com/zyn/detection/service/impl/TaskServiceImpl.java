package com.zyn.detection.service.impl;

import com.zyn.common.dto.response.DetectionResponse;
import com.zyn.common.dto.response.PageResponse;
import com.zyn.common.dto.response.TaskStatusResponse;
import com.zyn.common.entity.DetectionResult;
import com.zyn.common.entity.DetectionTask;
import com.zyn.common.enums.TaskStatus;
import com.zyn.common.exception.BusinessException;
import com.zyn.common.exception.ForbiddenException;
import com.zyn.common.exception.ResourceNotFoundException;
import com.zyn.detection.repository.DetectionResultRepository;
import com.zyn.detection.repository.DetectionTaskRepository;
import com.zyn.detection.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * 任务服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final DetectionTaskRepository taskRepository;
    private final DetectionResultRepository resultRepository;

    @Override
    public TaskStatusResponse getTaskStatus(String taskId, Long userId) {
        DetectionTask task = taskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("任务", taskId));

        // 权限检查
        if (!task.getUserId().equals(userId)) {
            throw ForbiddenException.resourceAccessDenied("任务");
        }

        // 构建响应
        TaskStatusResponse.TaskStatusResponseBuilder builder = TaskStatusResponse.builder()
                .taskId(task.getTaskId())
                .status(task.getStatus())
                .progress(task.getProgress())
                .message(getStatusMessage(task.getStatus()))
                .estimatedTimeRemaining(task.getEstimatedTimeSeconds());

        // 如果任务已完成，附加检测结果
        if (task.getStatus() == TaskStatus.COMPLETED) {
            resultRepository.findByTaskId(taskId).ifPresent(result -> {
                DetectionResponse detectionResponse = DetectionResponse.builder()
                        .detectionId(result.getId())
                        .videoId(result.getVideoId())
                        .result(result.getPrediction())
                        .confidence(result.getConfidence())
                        .modelVersion(result.getModelVersion())
                        .processingTimeMs(result.getProcessingTimeMs())
                        .framesAnalyzed(result.getFramesAnalyzed())
                        .artifacts(result.getArtifactsDetected() != null
                                ? Arrays.asList(result.getArtifactsDetected().split(","))
                                : null)
                        .features(result.getFeatures())
                        .createdAt(result.getCreatedAt())
                        .build();

                builder.result(detectionResponse);
            });
        }

        return builder.build();
    }

    @Override
    public DetectionTask getTaskDetail(String taskId, Long userId) {
        DetectionTask task = taskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("任务", taskId));

        // 权限检查
        if (!task.getUserId().equals(userId)) {
            throw ForbiddenException.resourceAccessDenied("任务");
        }

        return task;
    }

    @Override
    public PageResponse<DetectionTask> getMyTasks(Long userId, int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<DetectionTask> taskPage;

        if (status != null && !status.isEmpty()) {
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            taskPage = taskRepository.findByUserIdAndStatus(userId, taskStatus, pageable);
        } else {
            taskPage = taskRepository.findByUserId(userId, pageable);
        }

        return PageResponse.<DetectionTask>builder()
                .content(taskPage.getContent())
                .page(page)
                .size(size)
                .totalPages(taskPage.getTotalPages())
                .totalElements(taskPage.getTotalElements())
                .first(taskPage.isFirst())
                .last(taskPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public void cancelTask(String taskId, Long userId) {
        DetectionTask task = taskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("任务", taskId));

        // 权限检查
        if (!task.getUserId().equals(userId)) {
            throw ForbiddenException.resourceAccessDenied("任务");
        }

        // 只能取消待处理或处理中的任务
        if (task.getStatus() != TaskStatus.PENDING && task.getStatus() != TaskStatus.PROCESSING) {
            throw BusinessException.invalidStatus(
                    task.getStatus().name(),
                    "PENDING or PROCESSING"
            );
        }

        task.setStatus(TaskStatus.FAILED);
        task.setErrorMessage("用户取消");
        taskRepository.save(task);

        log.info("任务已取消 - 任务ID: {}", taskId);
    }

    /**
     * 获取状态描述
     */
    private String getStatusMessage(TaskStatus status) {
        return switch (status) {
            case PENDING -> "任务等待处理中";
            case PROCESSING -> "任务正在处理中";
            case COMPLETED -> "任务已完成";
            case FAILED -> "任务处理失败";
        };
    }
}
