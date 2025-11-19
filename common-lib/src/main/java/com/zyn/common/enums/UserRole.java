package com.zyn.common.enums;

/**
 * 用户角色枚举
 */
public enum UserRole {
    ROLE_USER("普通用户"),
    ROLE_ADMIN("管理员"),
    ROLE_MODERATOR("版主");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthority() {
        return this.name();
    }
}
