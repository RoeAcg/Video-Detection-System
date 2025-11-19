package com.zyn.audit.service;

import com.zyn.audit.dto.AuditStatisticsResponse;
import com.zyn.common.dto.response.PageResponse;
import com.zyn.common.entity.AuditLog;

import java.time.LocalDateTime;

/**
 * 审计日志服务接口
 */
public interface AuditLogService {

    /**
     * 保存审计日志
     */
    void saveAuditLog(AuditLog auditLog);

    /**
     * 异步保存审计日志
     */
    void saveAuditLogAsync(AuditLog auditLog);

    /**
     * 查询审计日志
     */
    PageResponse<AuditLog> queryAuditLogs(int page, int size, Long userId,
                                          String action, String resourceType,
                                          LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询用户审计日志
     */
    PageResponse<AuditLog> getUserAuditLogs(Long userId, int page, int size);

    /**
     * 获取审计日志详情
     */
    AuditLog getAuditLogDetail(Long logId);

    /**
     * 获取统计数据
     */
    AuditStatisticsResponse getStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 清理过期日志
     */
    int cleanupOldLogs(int daysToKeep);
}
