package com.zyn.common.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 举报请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportFakeRequest {

    @NotBlank(message = "Reason cannot be blank")
    @Size(max = 50, message = "Reason must be <= 50 characters")
    private String reason;

    @Size(max = 2000, message = "Evidence must be <= 2000 characters")
    private String evidence;
}
