package com.zyn.common.constant;

/**
 * 安全相关常量
 */
public final class SecurityConstants {

    private SecurityConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    // JWT相关
    public static final String JWT_TOKEN_HEADER = "Authorization";
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String JWT_CLAIM_USER_ID = "userId";
    public static final String JWT_CLAIM_USERNAME = "username";
    public static final String JWT_CLAIM_ROLES = "roles";

    // JWT过期时间（毫秒）
    public static final long JWT_EXPIRATION_MS = 86400000L;  // 24小时
    public static final long JWT_REFRESH_EXPIRATION_MS = 604800000L;  // 7天

    // 公开路径（无需认证）
    public static final String[] PUBLIC_URLS = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/api/health",
            "/actuator/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html"
    };

    // 用户路径（需要USER角色）
    public static final String[] USER_URLS = {
            "/api/videos/**",
            "/api/detections/**"
    };

    // 管理员路径（需要ADMIN角色）
    public static final String[] ADMIN_URLS = {
            "/api/admin/**",
            "/api/reports/**",
            "/api/audit/**"
    };

    // 密码规则
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 64;
    public static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

    // 用户名规则
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 50;
    public static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]{3,50}$";

    // 邮箱规则
    public static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    // 安全配置
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final long LOCKOUT_DURATION_MS = 900000L;  // 15分钟
}
