package com.zyn.common.exception;

import com.zyn.common.constant.AppConstants;

/**
 * 业务逻辑异常 (400)
 */
public class BusinessException extends BaseException {

    public BusinessException(String message) {
        super(AppConstants.BAD_REQUEST, message);
    }

    public BusinessException(int code, String message) {
        super(code, message);
    }

    public BusinessException(String message, Throwable cause) {
        super(AppConstants.BAD_REQUEST, message, cause);
    }

    // 通用业务异常工厂方法
    public static BusinessException invalidParameter(String paramName) {
        return new BusinessException(
                String.format("Invalid parameter: %s", paramName)
        );
    }

    public static BusinessException operationFailed(String operation) {
        return new BusinessException(
                String.format("Operation failed: %s", operation)
        );
    }

    public static BusinessException duplicateResource(String resourceType) {
        return new BusinessException(
                String.format("%s already exists", resourceType)
        );
    }

    public static BusinessException invalidStatus(String currentStatus, String expectedStatus) {
        return new BusinessException(
                String.format("Invalid status. Current: %s, Expected: %s",
                        currentStatus, expectedStatus)
        );
    }
}
