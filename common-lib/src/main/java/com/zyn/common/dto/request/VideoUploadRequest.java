package com.zyn.common.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 视频上传请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoUploadRequest {

    private String description;

    private String[] tags;
}
