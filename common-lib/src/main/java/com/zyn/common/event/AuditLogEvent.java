package com.zyn.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 审计日志事件
 * 用于Kafka消息传输
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String action;
    private String resourceType;
    private Long resourceId;
    private String details;
    private String ipAddress;
    private String userAgent;
    private String requestMethod;
    private String requestUri;
    private Integer statusCode;
    private LocalDateTime timestamp;
}
