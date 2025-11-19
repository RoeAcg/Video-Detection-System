package com.zyn.common.exception;

import com.zyn.common.constant.AppConstants;

/**
 * 文件上传异常 (400)
 */
public class FileUploadException extends BaseException {

    public FileUploadException(String message) {
        super(AppConstants.BAD_REQUEST, message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(AppConstants.BAD_REQUEST, message, cause);
    }

    public static FileUploadException fileTooLarge(long maxSize) {
        return new FileUploadException(
                String.format("File size exceeds maximum limit of %d bytes", maxSize)
        );
    }

    public static FileUploadException invalidFileType(String allowedTypes) {
        return new FileUploadException(
                String.format("Invalid file type. Allowed types: %s", allowedTypes)
        );
    }

    public static FileUploadException chunkMergeFailure() {
        return new FileUploadException("Failed to merge file chunks");
    }

    public static FileUploadException hashMismatch() {
        return new FileUploadException("File hash verification failed");
    }
}
