package com.zyn.common.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 检测任务事件DTO (Kafka消息)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetectionTaskEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String taskId;

    private Long videoId;

    private Long userId;

    private String videoPath;

    private String fileHash;

    private String mode = "standard";  // 检测模式: standard 或 aigc
    
    private Integer frameCount;

    private LocalDateTime timestamp;
}
