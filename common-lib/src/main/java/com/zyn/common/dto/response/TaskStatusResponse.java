package com.zyn.common.dto.response;

import com.zyn.common.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务状态响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatusResponse {

    private String taskId;

    private TaskStatus status;

    private Integer progress;

    private String message;

    private Integer estimatedTimeRemaining;

    private DetectionResponse result;
}
