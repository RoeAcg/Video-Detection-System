package com.zyn.websocket.consumer;

import com.zyn.common.constant.KafkaTopics;
import com.zyn.common.dto.event.DetectionCompletedEvent;
import com.zyn.websocket.dto.WebSocketMessage;
import com.zyn.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 通知消息Kafka消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final WebSocketService webSocketService;

    /**
     * 消费检测完成通知
     */
    @KafkaListener(
            topics = KafkaTopics.DETECTION_NOTIFICATIONS,
            groupId = KafkaTopics.CONSUMER_GROUP_NOTIFICATIONS
    )
    public void consumeDetectionCompleted(DetectionCompletedEvent event,
                                          Acknowledgment acknowledgment) {

        log.info("收到检测完成通知 - 任务ID: {}, 用户ID: {}, 结果: {}",
                event.getTaskId(), event.getUserId(), event.getResult());

        try {
            // 构建WebSocket消息
            Map<String, Object> data = new HashMap<>();
            data.put("taskId", event.getTaskId());
            data.put("detectionId", event.getDetectionId());
            data.put("result", event.getResult());
            data.put("confidence", event.getConfidence());

            WebSocketMessage message = WebSocketMessage.builder()
                    .type("DETECTION_COMPLETED")
                    .message("检测已完成")
                    .data(data)
                    .timestamp(System.currentTimeMillis())
                    .build();

            // 推送给指定用户
            webSocketService.sendToUser(event.getUserId().toString(), message);

            log.info("检测完成通知已推送 - 任务ID: {}, 用户ID: {}",
                    event.getTaskId(), event.getUserId());

            // 手动提交偏移量
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }

        } catch (Exception e) {
            log.error("处理检测完成通知失败 - 任务ID: {}, 错误: {}",
                    event.getTaskId(), e.getMessage(), e);

            // 仍然提交，避免阻塞
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }
}
