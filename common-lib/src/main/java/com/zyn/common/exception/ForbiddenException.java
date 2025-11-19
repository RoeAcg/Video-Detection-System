package com.zyn.common.exception;

import com.zyn.common.constant.AppConstants;

/**
 * 禁止访问异常 (403)
 */
public class ForbiddenException extends BaseException {

    public ForbiddenException(String message) {
        super(AppConstants.FORBIDDEN, message);
    }

    public ForbiddenException() {
        super(AppConstants.FORBIDDEN, "Access denied");
    }

    public static ForbiddenException insufficientPermissions() {
        return new ForbiddenException("Insufficient permissions to perform this action");
    }

    public static ForbiddenException resourceAccessDenied(String resourceType) {
        return new ForbiddenException(
                String.format("Access to %s is denied", resourceType)
        );
    }
}
