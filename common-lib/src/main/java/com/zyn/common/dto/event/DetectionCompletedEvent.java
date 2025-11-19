package com.zyn.common.dto.event;

import com.zyn.common.enums.DetectionResultEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 检测完成事件DTO (Kafka/WebSocket消息)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetectionCompletedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String taskId;

    private Long detectionId;

    private Long userId;

    private DetectionResultEnum result;

    private BigDecimal confidence;

    private LocalDateTime timestamp;
}
