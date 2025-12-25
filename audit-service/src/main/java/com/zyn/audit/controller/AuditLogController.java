package com.zyn.audit.controller;

import com.zyn.audit.dto.AuditStatisticsResponse;
import com.zyn.audit.service.AuditLogService;
import com.zyn.common.dto.response.PageResponse;
import com.zyn.common.entity.AuditLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 审计日志控制器
 * 需要管理员权限
 */
@Slf4j
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    /**
     * 查询审计日志列表
     */
    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<AuditLog>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        log.info("查询审计日志 - 页码: {}, 大小: {}, 用户ID: {}, 操作: {}",
                page, size, userId, action);

        PageResponse<AuditLog> response = auditLogService.queryAuditLogs(
                page, size, userId, action, resourceType, startTime, endTime);

        return ResponseEntity.ok(response);
    }

    /**
     * 查询用户操作日志
     */
    @GetMapping("/logs/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PageResponse<AuditLog>> getUserAuditLogs(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("查询用户审计日志 - 用户ID: {}, 页码: {}, 大小: {}", userId, page, size);

        PageResponse<AuditLog> response = auditLogService.getUserAuditLogs(userId, page, size);

        return ResponseEntity.ok(response);
    }

    /**
     * 查询审计日志详情
     */
    @GetMapping("/logs/{logId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuditLog> getAuditLogDetail(@PathVariable Long logId) {
        log.info("查询审计日志详情 - 日志ID: {}", logId);

        AuditLog auditLog = auditLogService.getAuditLogDetail(logId);

        return ResponseEntity.ok(auditLog);
    }

    /**
     * 获取审计统计数据
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuditStatisticsResponse> getStatistics(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        log.info("查询审计统计 - 开始时间: {}, 结束时间: {}", startTime, endTime);

        AuditStatisticsResponse statistics = auditLogService.getStatistics(startTime, endTime);

        return ResponseEntity.ok(statistics);
    }

    /**
     * 删除过期审计日志
     */
    @DeleteMapping("/logs/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cleanupOldLogs(@RequestParam int daysToKeep) {
        log.info("清理过期审计日志 - 保留天数: {}", daysToKeep);

        int deletedCount = auditLogService.cleanupOldLogs(daysToKeep);

        return ResponseEntity.ok(String.format("已删除 %d 条过期审计日志", deletedCount));
    }
}