package com.zyn.audit.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyn.audit.annotation.Audited;
import com.zyn.audit.service.AuditLogService;
import com.zyn.common.entity.AuditLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 审计切面
 * 自动记录带有@Audited注解的方法调用
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(audited)")
    public Object auditLog(ProceedingJoinPoint joinPoint, Audited audited) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        HttpServletRequest request = getHttpServletRequest();

        // 构建审计日志
        AuditLog auditLog = AuditLog.builder()
                .action(audited.action())
                .resourceType(audited.resourceType())
                .ipAddress(getClientIpAddress(request))
                .userAgent(getUserAgent(request))
                .requestMethod(getRequestMethod(request))
                .requestUri(getRequestUri(request))
                .build();

        Object result = null;
        int statusCode = 200;

        try {
            // 执行目标方法
            result = joinPoint.proceed();

            // 记录成功
            statusCode = 200;

        } catch (Exception e) {
            // 记录失败
            statusCode = 500;
            auditLog.setOldValue("Error: " + e.getMessage());
            throw e;

        } finally {
            // 完善审计日志
            auditLog.setStatusCode(statusCode);

            // 尝试获取资源ID
            Object resourceId = extractResourceId(joinPoint);
            if (resourceId != null) {
                auditLog.setResourceId(resourceId instanceof Long ? (Long) resourceId : null);
            }

            // 记录方法参数
            try {
                String params = objectMapper.writeValueAsString(joinPoint.getArgs());
                auditLog.setNewValue(params.length() > 500 ? params.substring(0, 500) : params);
            } catch (Exception e) {
                log.warn("无法序列化方法参数: {}", e.getMessage());
            }

            // 异步保存审计日志
            auditLogService.saveAuditLogAsync(auditLog);

            long duration = System.currentTimeMillis() - startTime;
            log.debug("审计日志已记录 - 操作: {}, 耗时: {}ms", audited.action(), duration);
        }

        return result;
    }

    /**
     * 获取HTTP请求
     */
    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    /**
     * 获取User-Agent
     */
    private String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return request.getHeader("User-Agent");
    }

    /**
     * 获取请求方法
     */
    private String getRequestMethod(HttpServletRequest request) {
        return request != null ? request.getMethod() : null;
    }

    /**
     * 获取请求URI
     */
    private String getRequestUri(HttpServletRequest request) {
        return request != null ? request.getRequestURI() : null;
    }

    /**
     * 提取资源ID（尝试从方法参数或返回值中提取）
     */
    private Object extractResourceId(ProceedingJoinPoint joinPoint) {
        // 从方法参数中查找Long类型的ID
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Long) {
                return arg;
            }
        }
        return null;
    }
}
