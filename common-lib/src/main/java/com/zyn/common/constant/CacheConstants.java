package com.zyn.common.constant;

/**
 * Redis缓存相关常量
 */
public final class CacheConstants {

    private CacheConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    // 缓存键前缀
    public static final String PREFIX_DETECTION_RESULT = "detection:result:";
    public static final String PREFIX_VIDEO_HASH = "video:hash:";
    public static final String PREFIX_USER_SESSION = "user:session:";
    public static final String PREFIX_TASK_STATUS = "task:status:";
    public static final String PREFIX_USER_INFO = "user:info:";

    // 缓存过期时间（秒）
    public static final long TTL_ONE_MINUTE = 60L;
    public static final long TTL_FIVE_MINUTES = 300L;
    public static final long TTL_ONE_HOUR = 3600L;
    public static final long TTL_ONE_DAY = 86400L;
    public static final long TTL_ONE_WEEK = 604800L;

    // 默认TTL
    public static final long DEFAULT_TTL = TTL_ONE_DAY;

    // 具体业务缓存TTL
    public static final long DETECTION_RESULT_TTL = TTL_ONE_DAY;
    public static final long VIDEO_HASH_TTL = TTL_ONE_DAY;
    public static final long USER_SESSION_TTL = TTL_ONE_HOUR;
    public static final long TASK_STATUS_TTL = TTL_FIVE_MINUTES;

    // 缓存键构建方法
    public static String buildDetectionResultKey(Long id) {
        return PREFIX_DETECTION_RESULT + id;
    }

    public static String buildVideoHashKey(String hash) {
        return PREFIX_VIDEO_HASH + hash;
    }

    public static String buildUserSessionKey(String token) {
        return PREFIX_USER_SESSION + token;
    }

    public static String buildTaskStatusKey(String taskId) {
        return PREFIX_TASK_STATUS + taskId;
    }

    public static String buildUserInfoKey(Long userId) {
        return PREFIX_USER_INFO + userId;
    }
}
