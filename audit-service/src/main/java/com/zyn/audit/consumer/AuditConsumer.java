package com.zyn.audit.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyn.audit.repository.AuditLogRepository;
import com.zyn.common.entity.AuditLog;
import com.zyn.common.event.AuditLogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditConsumer {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "audit-logs", groupId = "audit-group")
    public void consume(String message) {
        log.info("收到审计日志消息: {}", message);
        try {
            AuditLogEvent event = objectMapper.readValue(message, AuditLogEvent.class);
            
            AuditLog auditLog = new AuditLog();
            auditLog.setUserId(event.getUserId());
            auditLog.setAction(event.getAction());
            auditLog.setResourceType(event.getResourceType());
            auditLog.setResourceId(event.getResourceId());
            auditLog.setNewValue(event.getDetails()); 
            
            auditLog.setIpAddress(event.getIpAddress());
            auditLog.setUserAgent(event.getUserAgent());
            auditLog.setRequestMethod(event.getRequestMethod());
            auditLog.setRequestUri(event.getRequestUri());
            auditLog.setStatusCode(event.getStatusCode());
            
            auditLogRepository.save(auditLog);
            log.info("审计日志保存成功: {}", auditLog);

        } catch (Exception e) {
            log.error("处理审计日志消息失败: {}", e.getMessage(), e);
        }
    }
}
