package com.zyn.auth.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyn.common.event.AuditLogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC = "audit-logs";

    public void sendAuditLog(AuditLogEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, message);
            log.info("发送审计日志到Kafka: {}", message);
        } catch (Exception e) {
            log.error("发送审计日志失败: {}", e.getMessage(), e);
        }
    }
}
