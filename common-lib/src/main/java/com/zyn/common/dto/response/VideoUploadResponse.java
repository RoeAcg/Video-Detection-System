package com.zyn.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 视频上传响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoUploadResponse {

    private String taskId;

    private String uploadStatus;

    private Integer uploadProgress;

    private String estimatedTime;

    private LocalDateTime createdAt;
}
