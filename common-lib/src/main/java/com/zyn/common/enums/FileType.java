package com.zyn.common.enums;

/**
 * 文件类型枚举
 */
public enum FileType {
    VIDEO_MP4("video/mp4", ".mp4"),
    VIDEO_AVI("video/x-msvideo", ".avi"),
    VIDEO_MOV("video/quicktime", ".mov"),
    VIDEO_MKV("video/x-matroska", ".mkv"),
    VIDEO_WEBM("video/webm", ".webm"),
    
    // 图片类型
    IMAGE_JPEG("image/jpeg", ".jpg"),
    IMAGE_JPG("image/jpeg", ".jpeg"),
    IMAGE_PNG("image/png", ".png"),
    IMAGE_BMP("image/bmp", ".bmp"),
    IMAGE_GIF("image/gif", ".gif");

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
    
    /**
     * 判断是否为图片类型
     */
    public boolean isImage() {
        return this.name().startsWith("IMAGE_");
    }
    
    /**
     * 判断是否为视频类型
     */
    public boolean isVideo() {
        return this.name().startsWith("VIDEO_");
    }
}
