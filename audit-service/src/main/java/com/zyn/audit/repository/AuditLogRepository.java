package com.zyn.audit.repository;

import com.zyn.common.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志仓库
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * 根据用户ID查询
     */
    Page<AuditLog> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据操作查询
     */
    Page<AuditLog> findByAction(String action, Pageable pageable);

    /**
     * 根据用户ID和操作查询
     */
    Page<AuditLog> findByUserIdAndAction(Long userId, String action, Pageable pageable);

    /**
     * 根据时间范围查询
     */
    Page<AuditLog> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * 统计时间范围内的日志数
     */
    long countByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计时间范围内的独立用户数
     */
    @Query("SELECT COUNT(DISTINCT a.userId) FROM AuditLog a WHERE a.createdAt BETWEEN :startTime AND :endTime")
    long countDistinctUserIdByCreatedAtBetween(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 按操作分组统计
     */
    @Query("SELECT a.action, COUNT(a) FROM AuditLog a WHERE a.createdAt BETWEEN :startTime AND :endTime GROUP BY a.action")
    List<Object[]> countByActionGrouped(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 按资源类型分组统计
     */
    @Query("SELECT a.resourceType, COUNT(a) FROM AuditLog a WHERE a.createdAt BETWEEN :startTime AND :endTime GROUP BY a.resourceType")
    List<Object[]> countByResourceTypeGrouped(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 删除指定日期之前的日志
     */
    int deleteByCreatedAtBefore(LocalDateTime date);
}
