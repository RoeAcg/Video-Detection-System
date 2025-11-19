package com.zyn.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 密钥
     */
    private String secret = "mySecretKeyForJWTTokenGenerationAndValidation12345678901234567890";

    /**
     * Token 过期时间（毫秒）
     */
    private Long expiration = 86400000L;  // 24小时

    /**
     * Token 刷新时间（毫秒）
     */
    private Long refreshExpiration = 604800000L;  // 7天
}
