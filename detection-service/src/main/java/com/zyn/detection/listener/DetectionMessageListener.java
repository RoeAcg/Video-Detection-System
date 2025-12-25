package com.zyn.detection.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyn.aiclient.dto.AiDetectionRequest;
import com.zyn.aiclient.dto.AiDetectionResponse;
import com.zyn.aiclient.feign.AiServiceClient;
import com.zyn.common.constant.KafkaTopics;
import com.zyn.common.dto.event.DetectionTaskEvent;
import com.zyn.common.entity.DetectionResult;
import com.zyn.common.entity.DetectionTask;
import com.zyn.common.enums.DetectionResultEnum;
import com.zyn.common.enums.TaskStatus;
import com.zyn.detection.repository.DetectionResultRepository;
import com.zyn.detection.repository.DetectionTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 监听检测任务消息并调用AI服务进行处理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DetectionMessageListener {

    private final AiServiceClient aiServiceClient;
    private final DetectionTaskRepository taskRepository;
    private final DetectionResultRepository resultRepository;

    // @KafkaListener(topics = KafkaTopics.DETECTION_TASKS, groupId = "detection-group")
    public void handleDetectionTask(DetectionTaskEvent event) {
        log.info("收到检测任务: taskId={}, videoPath={}", event.getTaskId(), event.getVideoPath());

        // 1. 获取并更新任务状态为处理中
        Optional<DetectionTask> taskOpt = taskRepository.findByTaskId(event.getTaskId());
        if (taskOpt.isEmpty()) {
            log.warn("任务不存在，忽略消息: {}", event.getTaskId());
            return;
        }

        DetectionTask task = taskOpt.get();
        task.setStatus(TaskStatus.PROCESSING);
        task.setStartedAt(LocalDateTime.now());
        task.setProgress(10); // 开始处理
        taskRepository.save(task);

        try {
            // 2. 调用AI服务
            AiDetectionRequest request = AiDetectionRequest.builder()
                    .taskId(event.getTaskId())
                    .videoPath(event.getVideoPath())
                    .fileHash(event.getFileHash())
                    .mode("standard") // 默认使用 standard 模式
                    .build();

            log.info("调用AI服务进行检测: {}", request);
            AiDetectionResponse response = aiServiceClient.detect(request);
            log.info("AI服务返回结果: {}", response);

            // 3. 保存检测结果
            saveDetectionResult(task, response);

            // 4. 更新任务状态为完成
            task.setStatus(TaskStatus.COMPLETED);
            task.setProgress(100);
            task.setCompletedAt(LocalDateTime.now());
            taskRepository.save(task);

            log.info("任务处理完成: {}", event.getTaskId());

        } catch (Exception e) {
            log.error("处理检测任务失败: {}", event.getTaskId(), e);
            
            // 更新任务状态为失败
            task.setStatus(TaskStatus.FAILED);
            task.setErrorMessage(e.getMessage());
            task.setCompletedAt(LocalDateTime.now());
            taskRepository.save(task);
        }
    }

    private void saveDetectionResult(DetectionTask task, AiDetectionResponse response) {
        DetectionResultEnum resultEnum = DetectionResultEnum.UNCERTAIN;
        if ("FAKE".equalsIgnoreCase(response.getResult())) {
            resultEnum = DetectionResultEnum.FAKE;
        } else if ("REAL".equalsIgnoreCase(response.getResult())) {
            resultEnum = DetectionResultEnum.AUTHENTIC;
        }

        DetectionResult result = DetectionResult.builder()
                .taskId(task.getTaskId())
                .videoId(task.getVideoId())
                .userId(task.getUserId())
                .prediction(resultEnum)
                .confidence(response.getConfidence())
                .modelVersion(response.getModelVersion())
                .processingTimeMs(response.getProcessingTimeMs())
                .framesAnalyzed(response.getFramesAnalyzed())
                .features(response.getFeatures())
                .artifactsDetected(response.getArtifacts() != null ? String.join(",", response.getArtifacts()) : "")
                .build();
        
        resultRepository.save(result);
    }
}
