package com.zyn.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 审计统计响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditStatisticsResponse {

    /**
     * 总日志数
     */
    private Long totalLogs;

    /**
     * 独立用户数
     */
    private Long uniqueUsers;

    /**
     * 按操作类型统计
     */
    private Map<String, Long> actionCounts;

    /**
     * 按资源类型统计
     */
    private Map<String, Long> resourceCounts;

    /**
     * 统计开始时间
     */
    private LocalDateTime startTime;

    /**
     * 统计结束时间
     */
    private LocalDateTime endTime;
}
