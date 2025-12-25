package com.zyn.common.enums;

/**
 * 检测模式枚举
 */
public enum DetectionMode {
    /**
     * 标准模式 - 人脸伪造检测 (Effort模型)
     */
    STANDARD,
    
    /**
     * AIGC模式 - 通用生成内容检测 (DRCT模型)
     */
    AIGC
}
