package com.zyn.common.enums;

/**
 * 文件类型枚举
 */
public enum FileType {
    VIDEO_MP4("video/mp4", ".mp4"),
    VIDEO_AVI("video/x-msvideo", ".avi"),
    VIDEO_MOV("video/quicktime", ".mov"),
    VIDEO_MKV("video/x-matroska", ".mkv"),
    VIDEO_WEBM("video/webm", ".webm");

    private final String mimeType;
    private final String extension;

    FileType(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public static FileType fromMimeType(String mimeType) {
        for (FileType type : values()) {
            if (type.mimeType.equals(mimeType)) {
                return type;
            }
        }
        return null;
    }

    public static FileType fromExtension(String extension) {
        for (FileType type : values()) {
            if (type.extension.equalsIgnoreCase(extension)) {
                return type;
            }
        }
        return null;
    }
}
