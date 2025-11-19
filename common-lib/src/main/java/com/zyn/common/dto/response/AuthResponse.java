package com.zyn.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 认证响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;

    private String tokenType = "Bearer";

    private Long expiresIn;

    private Long userId;

    private String username;

    private String email;

    private Set<String> roles;
}
