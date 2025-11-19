package com.zyn.common.enums;

/**
 * 检测结果枚举
 */
public enum DetectionResultEnum {
    AUTHENTIC("真实"),
    FAKE("伪造"),
    UNCERTAIN("不确定");

    private final String description;

    DetectionResultEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
