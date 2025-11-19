package com.zyn.audit.annotation;

import java.lang.annotation.*;

/**
 * 审计注解
 * 标记需要记录审计日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Audited {

    /**
     * 操作名称
     */
    String action();

    /**
     * 资源类型
     */
    String resourceType() default "";

    /**
     * 描述
     */
    String description() default "";
}
