package com.zyn.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyn.common.annotation.AuditLog;
import com.zyn.common.event.AuditLogEvent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * 审计日志切面
 */
@Aspect
@Component
@Slf4j
public class AuditAspect {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // SpEL parsers
    private final org.springframework.expression.ExpressionParser parser = new org.springframework.expression.spel.standard.SpelExpressionParser();
    private final org.springframework.core.ParameterNameDiscoverer parameterNameDiscoverer = new org.springframework.core.DefaultParameterNameDiscoverer();

    @Around("@annotation(auditLog)")
    public Object around(ProceedingJoinPoint point, AuditLog auditLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        String errorMsg = null;

        try {
            result = point.proceed();
            return result;
        } catch (Throwable e) {
            errorMsg = e.getMessage();
            throw e;
        } finally {
            try {
                handleAudit(point, auditLog, result, errorMsg);
            } catch (Exception e) {
                log.error("Failed to record audit log", e);
            }
        }
    }

    private void handleAudit(ProceedingJoinPoint point, AuditLog auditLog, Object result, String errorMsg) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

            // 获取用户信息
            Long userId = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof com.zyn.common.security.CustomUserDetails) {
                userId = ((com.zyn.common.security.CustomUserDetails) authentication.getPrincipal()).getId();
            } else if (authentication != null && authentication.getDetails() instanceof Map) {
                // Fallback (保留之前的逻辑，以防万一)
                Map<?, ?> details = (Map<?, ?>) authentication.getDetails();
                if (details.containsKey("userId")) {
                    userId = (Long) details.get("userId");
                }
            }

            // 构建事件
            AuditLogEvent event = AuditLogEvent.builder()
                    .userId(userId)
                    .action(auditLog.action())
                    .resourceType(auditLog.resourceType())
                    .timestamp(LocalDateTime.now())
                    .build();

            if (request != null) {
                event.setIpAddress(getClientIp(request));
                event.setUserAgent(request.getHeader("User-Agent"));
                event.setRequestMethod(request.getMethod());
                event.setRequestUri(request.getRequestURI());
            }

            // 设置状态码
            // 简单逻辑：如果有 errorMsg 则是 500，否则 200
            event.setStatusCode(errorMsg != null ? 500 : 200);

            // 解析 Details (SpEL or Error or Result)
            if (errorMsg != null) {
                event.setDetails("Error: " + errorMsg);
            } else {
                // 优先使用 SpEL 解析 details
                String details = resolveDetails(auditLog.details(), point, result);
                if (details != null && !details.isEmpty()) {
                    event.setDetails(details);
                } else if (result != null) {
                     // 没配置 SpEL，则 Fallback 到 JSON 序列化 Result (保持向后兼容，或者可以去掉)
                     try {
                         String json = objectMapper.writeValueAsString(result);
                         if (json.length() > 500) json = json.substring(0, 500) + "...";
                         event.setDetails("Result: " + json);
                    } catch (Exception ignored) {}
                }
            }

            // 发送 Kafka
            // 注意：这里需要确保 "audit-logs" topic 存在
            kafkaTemplate.send("audit-logs", event);
            log.debug("Sent audit log: {}", event);

        } catch (Exception e) {
            log.error("Error building audit log event", e);
        }
    }

    /**
     * 解析 SpEL 表达式
     */
    private String resolveDetails(String spEl, ProceedingJoinPoint point, Object result) {
        if (spEl == null || spEl.isEmpty()) {
            return null;
        }
        try {
            org.springframework.expression.EvaluationContext context = new org.springframework.expression.spel.support.StandardEvaluationContext();
            
            // 将方法参数放入 Context
            Object[] args = point.getArgs();
            org.aspectj.lang.reflect.MethodSignature signature = (org.aspectj.lang.reflect.MethodSignature) point.getSignature();
            String[] paramNames = parameterNameDiscoverer.getParameterNames(signature.getMethod());
            
            if (paramNames != null) {
                for (int i = 0; i < args.length; i++) {
                    context.setVariable(paramNames[i], args[i]);
                }
            }
            
            // 将 result 也放入，以便后置处理能访问返回值 (key: result, or #result?)
            // Standard behavior often uses #result for @AfterReturning
            if (result != null) {
                context.setVariable("result", result);
            }

            return parser.parseExpression(spEl).getValue(context, String.class);
        } catch (Exception e) {
            log.warn("Failed to parse SpEL '{}': {}", spEl, e.getMessage());
            return spEl; // 解析失败则返回原字符串
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
