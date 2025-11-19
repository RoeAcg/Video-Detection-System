package com.zyn.common.exception;

import com.zyn.common.constant.AppConstants;

/**
 * 未授权异常 (401)
 */
public class UnauthorizedException extends BaseException {

    public UnauthorizedException(String message) {
        super(AppConstants.UNAUTHORIZED, message);
    }

    public UnauthorizedException() {
        super(AppConstants.UNAUTHORIZED, "Unauthorized access");
    }

    public static UnauthorizedException invalidToken() {
        return new UnauthorizedException("Invalid or expired token");
    }

    public static UnauthorizedException invalidCredentials() {
        return new UnauthorizedException("Invalid username or password");
    }
}
