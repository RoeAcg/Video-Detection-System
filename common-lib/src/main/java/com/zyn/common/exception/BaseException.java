package com.zyn.common.exception;

import lombok.Getter;

/**
 * 基础异常类
 */
@Getter
public class BaseException extends RuntimeException {

    private final int code;
    private final String message;

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}
