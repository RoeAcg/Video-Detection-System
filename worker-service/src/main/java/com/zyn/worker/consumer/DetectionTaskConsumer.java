package com.zyn.worker.consumer;

import com.zyn.common.constant.KafkaTopics;
import com.zyn.common.dto.event.DetectionTaskEvent;
import com.zyn.worker.service.DetectionProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 检测任务Kafka消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DetectionTaskConsumer {

    private final DetectionProcessor detectionProcessor;

    /**
     * 消费检测任务消息
     */
    @KafkaListener(
            topics = KafkaTopics.DETECTION_TASKS,
            groupId = KafkaTopics.CONSUMER_GROUP_WORKERS,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeDetectionTask(
            @Payload DetectionTaskEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        log.info("收到检测任务 - 任务ID: {}, 视频ID: {}, 分区: {}, 偏移量: {}",
                event.getTaskId(), event.getVideoId(), partition, offset);

        try {
            // 处理检测任务
            detectionProcessor.processDetectionTask(event);

            // 手动提交偏移量
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }

            log.info("检测任务处理完成 - 任务ID: {}", event.getTaskId());

        } catch (Exception e) {
            log.error("检测任务处理失败 - 任务ID: {}, 错误: {}",
                    event.getTaskId(), e.getMessage(), e);

            // 这里可以实现重试逻辑或发送到死信队列
            // 暂时手动提交，避免阻塞其他消息
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }
}
