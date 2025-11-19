package com.zyn.worker.service.impl;

import com.zyn.aiclient.dto.AiDetectionRequest;
import com.zyn.aiclient.dto.AiDetectionResponse;
import com.zyn.aiclient.exception.AiServiceException;
import com.zyn.aiclient.feign.AiServiceClient;
import com.zyn.common.constant.KafkaTopics;
import com.zyn.common.dto.event.DetectionCompletedEvent;
import com.zyn.common.dto.event.DetectionTaskEvent;
import com.zyn.common.entity.DetectionResult;
import com.zyn.common.entity.DetectionTask;
import com.zyn.common.enums.DetectionResultEnum;
import com.zyn.common.enums.TaskStatus;
import com.zyn.worker.repository.DetectionResultRepository;
import com.zyn.worker.repository.DetectionTaskRepository;
import com.zyn.worker.service.DetectionProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 检测处理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DetectionProcessorImpl implements DetectionProcessor {

    private final AiServiceClient aiServiceClient;
    private final DetectionTaskRepository taskRepository;
    private final DetectionResultRepository resultRepository;
    private final KafkaTemplate<String, DetectionCompletedEvent> kafkaTemplate;

    @Override
    @Transactional
    public void processDetectionTask(DetectionTaskEvent event) {
        String taskId = event.getTaskId();

        // 1. 创建或更新任务记录
        DetectionTask task = createOrUpdateTask(event);

        try {
            // 2. 更新任务状态为处理中
            updateTaskStatus(task, TaskStatus.PROCESSING, 0, null);

            // 3. 调用AI服务进行检测
            log.info("开始调用AI服务 - 任务ID: {}, 视频路径: {}", taskId, event.getVideoPath());
            AiDetectionResponse aiResponse = callAiService(event);

            // 4. 保存检测结果
            DetectionResult result = saveDetectionResult(event, aiResponse);

            // 5. 更新任务状态为完成
            updateTaskStatus(task, TaskStatus.COMPLETED, 100, null);

            // 6. 发送完成通知
            sendCompletedNotification(event, result, aiResponse);

            log.info("检测任务完成 - 任务ID: {}, 结果: {}, 置信度: {}",
                    taskId, aiResponse.getResult(), aiResponse.getConfidence());

        } catch (AiServiceException e) {
            log.error("AI服务调用失败 - 任务ID: {}, 错误: {}", taskId, e.getMessage());
            handleTaskFailure(task, "AI服务调用失败: " + e.getMessage());

        } catch (Exception e) {
            log.error("检测任务处理异常 - 任务ID: {}, 错误: {}", taskId, e.getMessage(), e);
            handleTaskFailure(task, "处理异常: " + e.getMessage());
        }
    }

    /**
     * 创建或更新任务记录
     */
    private DetectionTask createOrUpdateTask(DetectionTaskEvent event) {
        return taskRepository.findByTaskId(event.getTaskId())
                .orElseGet(() -> {
                    DetectionTask newTask = DetectionTask.builder()
                            .taskId(event.getTaskId())
                            .videoId(event.getVideoId())
                            .userId(event.getUserId())
                            .status(TaskStatus.PENDING)
                            .progress(0)
                            .retryCount(0)
                            .build();
                    return taskRepository.save(newTask);
                });
    }

    /**
     * 更新任务状态
     */
    private void updateTaskStatus(DetectionTask task, TaskStatus status,
                                  Integer progress, String errorMessage) {
        task.setStatus(status);
        task.setProgress(progress);

        if (status == TaskStatus.PROCESSING && task.getStartedAt() == null) {
            task.setStartedAt(LocalDateTime.now());
        }

        if (status == TaskStatus.COMPLETED || status == TaskStatus.FAILED) {
            task.setCompletedAt(LocalDateTime.now());
        }

        if (errorMessage != null) {
            task.setErrorMessage(errorMessage);
        }

        taskRepository.save(task);
    }

    /**
     * 调用AI服务
     */
    private AiDetectionResponse callAiService(DetectionTaskEvent event) {
        AiDetectionRequest request = AiDetectionRequest.builder()
                .taskId(event.getTaskId())
                .videoPath(event.getVideoPath())
                .fileHash(event.getFileHash())
                .mode("standard")
                .frameRate(5)
                .maxFrames(300)
                .includeFeatures(true)
                .timeoutSeconds(120)
                .build();

        return aiServiceClient.detect(request);
    }

    /**
     * 保存检测结果
     */
    private DetectionResult saveDetectionResult(DetectionTaskEvent event,
                                                AiDetectionResponse aiResponse) {
        DetectionResult result = DetectionResult.builder()
                .taskId(event.getTaskId())
                .videoId(event.getVideoId())
                .userId(event.getUserId())
                .prediction(DetectionResultEnum.valueOf(aiResponse.getResult()))
                .confidence(aiResponse.getConfidence())
                .modelVersion(aiResponse.getModelVersion())
                .processingTimeMs(aiResponse.getProcessingTimeMs())
                .framesAnalyzed(aiResponse.getFramesAnalyzed())
                .features(aiResponse.getFeatures())
                .artifactsDetected(aiResponse.getArtifacts() != null
                        ? String.join(",", aiResponse.getArtifacts())
                        : null)
                .build();

        return resultRepository.save(result);
    }

    /**
     * 发送完成通知
     */
    private void sendCompletedNotification(DetectionTaskEvent event,
                                           DetectionResult result,
                                           AiDetectionResponse aiResponse) {
        DetectionCompletedEvent completedEvent = DetectionCompletedEvent.builder()
                .taskId(event.getTaskId())
                .detectionId(result.getId())
                .userId(event.getUserId())
                .result(DetectionResultEnum.valueOf(aiResponse.getResult()))
                .confidence(aiResponse.getConfidence())
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(KafkaTopics.DETECTION_NOTIFICATIONS, event.getTaskId(), completedEvent);
        log.info("发送完成通知 - 任务ID: {}", event.getTaskId());
    }

    /**
     * 处理任务失败
     */
    private void handleTaskFailure(DetectionTask task, String errorMessage) {
        // 检查是否需要重试
        if (task.getRetryCount() < KafkaTopics.MAX_RETRY_ATTEMPTS) {
            task.setRetryCount(task.getRetryCount() + 1);
            task.setStatus(TaskStatus.PENDING);
            task.setErrorMessage("重试中: " + errorMessage);
            taskRepository.save(task);

            log.warn("任务将重试 - 任务ID: {}, 重试次数: {}/{}",
                    task.getTaskId(), task.getRetryCount(), KafkaTopics.MAX_RETRY_ATTEMPTS);
        } else {
            // 超过最大重试次数，标记为失败
            updateTaskStatus(task, TaskStatus.FAILED, task.getProgress(), errorMessage);
            log.error("任务失败（已达最大重试次数） - 任务ID: {}", task.getTaskId());
        }
    }
}