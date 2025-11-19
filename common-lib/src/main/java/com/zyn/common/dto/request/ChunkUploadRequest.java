package com.zyn.common.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分块上传请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChunkUploadRequest {

    @NotBlank(message = "File ID cannot be blank")
    private String fileId;

    @NotNull(message = "Chunk index cannot be null")
    @Min(value = 0, message = "Chunk index must be >= 0")
    private Integer chunkIndex;

    @NotNull(message = "Total chunks cannot be null")
    @Min(value = 1, message = "Total chunks must be >= 1")
    private Integer totalChunks;

    @NotBlank(message = "Chunk hash cannot be blank")
    private String chunkHash;

    private Long chunkSize;
}
