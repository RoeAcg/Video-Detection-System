package com.zyn.aiclient.exception;

/**
 * AI服务异常
 * 封装AI服务调用过程中的各种异常
 */
public class AiServiceException extends RuntimeException {

    private final int statusCode;
    private final String errorCode;

    public AiServiceException(String message) {
        super(message);
        this.statusCode = 500;
        this.errorCode = "AI_SERVICE_ERROR";
    }

    public AiServiceException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 500;
        this.errorCode = "AI_SERVICE_ERROR";
    }

    public AiServiceException(int statusCode, String errorCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public AiServiceException(int statusCode, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // 工厂方法
    public static AiServiceException timeout() {
        return new AiServiceException(408, "AI_TIMEOUT", "AI服务响应超时");
    }

    public static AiServiceException serviceUnavailable() {
        return new AiServiceException(503, "AI_UNAVAILABLE", "AI服务暂时不可用");
    }

    public static AiServiceException invalidRequest(String reason) {
        return new AiServiceException(400, "INVALID_REQUEST", "请求参数无效: " + reason);
    }

    public static AiServiceException processingFailed(String reason) {
        return new AiServiceException(500, "PROCESSING_FAILED", "AI处理失败: " + reason);
    }
}
