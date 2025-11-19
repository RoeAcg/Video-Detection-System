package com.zyn.audit.service.impl;

import com.zyn.audit.dto.AuditStatisticsResponse;
import com.zyn.audit.repository.AuditLogRepository;
import com.zyn.audit.service.AuditLogService;
import com.zyn.common.dto.response.PageResponse;
import com.zyn.common.entity.AuditLog;
import com.zyn.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 审计日志服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public void saveAuditLog(AuditLog auditLog) {
        auditLogRepository.save(auditLog);
        log.debug("审计日志已保存 - 操作: {}, 用户ID: {}",
                auditLog.getAction(), auditLog.getUserId());
    }

    @Override
    @Async
    @Transactional
    public void saveAuditLogAsync(AuditLog auditLog) {
        try {
            auditLogRepository.save(auditLog);
            log.debug("审计日志已异步保存 - 操作: {}", auditLog.getAction());
        } catch (Exception e) {
            log.error("保存审计日志失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public PageResponse<AuditLog> queryAuditLogs(
            int page, int size, Long userId, String action, String resourceType,
            LocalDateTime startTime, LocalDateTime endTime) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<AuditLog> auditLogPage;

        // 根据不同条件查询
        if (userId != null && action != null) {
            auditLogPage = auditLogRepository.findByUserIdAndAction(userId, action, pageable);
        } else if (userId != null) {
            auditLogPage = auditLogRepository.findByUserId(userId, pageable);
        } else if (action != null) {
            auditLogPage = auditLogRepository.findByAction(action, pageable);
        } else if (startTime != null && endTime != null) {
            auditLogPage = auditLogRepository.findByCreatedAtBetween(startTime, endTime, pageable);
        } else {
            auditLogPage = auditLogRepository.findAll(pageable);
        }

        return PageResponse.<AuditLog>builder()
                .content(auditLogPage.getContent())
                .page(page)
                .size(size)
                .totalPages(auditLogPage.getTotalPages())
                .totalElements(auditLogPage.getTotalElements())
                .first(auditLogPage.isFirst())
                .last(auditLogPage.isLast())
                .build();
    }

    @Override
    public PageResponse<AuditLog> getUserAuditLogs(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<AuditLog> auditLogPage = auditLogRepository.findByUserId(userId, pageable);

        return PageResponse.<AuditLog>builder()
                .content(auditLogPage.getContent())
                .page(page)
                .size(size)
                .totalPages(auditLogPage.getTotalPages())
                .totalElements(auditLogPage.getTotalElements())
                .first(auditLogPage.isFirst())
                .last(auditLogPage.isLast())
                .build();
    }

    @Override
    public AuditLog getAuditLogDetail(Long logId) {
        return auditLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("审计日志", logId));
    }

    @Override
    public AuditStatisticsResponse getStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        // 如果没有指定时间范围，默认查询最近7天
        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(7);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }

        // 总日志数
        long totalLogs = auditLogRepository.countByCreatedAtBetween(startTime, endTime);

        // 独立用户数
        long uniqueUsers = auditLogRepository.countDistinctUserIdByCreatedAtBetween(startTime, endTime);

        // 按操作分组统计
        List<Object[]> actionStats = auditLogRepository.countByActionGrouped(startTime, endTime);
        Map<String, Long> actionCounts = actionStats.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));

        // 按资源类型分组统计
        List<Object[]> resourceStats = auditLogRepository.countByResourceTypeGrouped(startTime, endTime);
        Map<String, Long> resourceCounts = resourceStats.stream()
                .filter(arr -> arr[0] != null)
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));

        return AuditStatisticsResponse.builder()
                .totalLogs(totalLogs)
                .uniqueUsers(uniqueUsers)
                .actionCounts(actionCounts)
                .resourceCounts(resourceCounts)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    @Override
    @Transactional
    public int cleanupOldLogs(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);

        int deletedCount = auditLogRepository.deleteByCreatedAtBefore(cutoffDate);

        log.info("清理过期审计日志完成 - 删除: {} 条, 截止日期: {}", deletedCount, cutoffDate);

        return deletedCount;
    }
}
