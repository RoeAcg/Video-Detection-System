package com.zyn.common.exception;

import com.zyn.common.constant.AppConstants;

/**
 * 资源未找到异常 (404)
 */
public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String message) {
        super(AppConstants.NOT_FOUND, message);
    }

    public ResourceNotFoundException(String resourceType, Long id) {
        super(AppConstants.NOT_FOUND,
                String.format("%s with id %d not found", resourceType, id));
    }

    public ResourceNotFoundException(String resourceType, String identifier) {
        super(AppConstants.NOT_FOUND,
                String.format("%s with identifier %s not found", resourceType, identifier));
    }
}
