package com.zyn.common.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 完成上传请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteUploadRequest {

    @NotBlank(message = "File ID cannot be blank")
    private String fileId;

    @NotBlank(message = "File name cannot be blank")
    private String fileName;

    @NotNull(message = "Total chunks cannot be null")
    @Min(value = 1, message = "Total chunks must be >= 1")
    private Integer totalChunks;

    private String fileHash;
}
