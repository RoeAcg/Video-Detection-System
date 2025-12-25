package com.zyn.common.annotation;

import java.lang.annotation.*;

/**
 * 审计日志注解
 * 用于标记需要记录审计日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {
    
    /**
     * 操作名称 (例如: LOGIN, UPLOAD)
     */
    String action() default "";
    
    /**
     * 资源类型 (例如: USER, VIDEO)
     */
    String resourceType() default "";

    /**
     * 操作详情描述 (支持 SpEL 表达式, 例如: "#request.username")
     */
    String details() default "";
}
