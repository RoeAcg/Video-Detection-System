package com.zyn.common.enums;

/**
 * 举报状态枚举
 */
public enum ReportStatus {
    SUBMITTED("已提交"),
    UNDER_REVIEW("审核中"),
    RESOLVED("已处理"),
    REJECTED("已拒绝");

    private final String description;

    ReportStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
