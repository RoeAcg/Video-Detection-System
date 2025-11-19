package com.zyn.common.constant;

/**
 * 应用全局常量
 */
public final class AppConstants {

    private AppConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    // 应用信息
    public static final String APP_NAME = "Video Detection System";
    public static final String APP_VERSION = "1.0.0";
    public static final String API_PREFIX = "/api";
    public static final String API_VERSION = "v1";

    // 分页参数
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_FIELD = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    // 文件上传限制
    public static final long MAX_FILE_SIZE = 2L * 1024 * 1024 * 1024;  // 2GB
    public static final long MAX_CHUNK_SIZE = 5L * 1024 * 1024;  // 5MB
    public static final int MAX_CHUNKS = 500;

    // 日期时间格式
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss";

    // 字符集
    public static final String DEFAULT_CHARSET = "UTF-8";

    // 通用状态
    public static final int SUCCESS = 200;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int INTERNAL_ERROR = 500;
}
