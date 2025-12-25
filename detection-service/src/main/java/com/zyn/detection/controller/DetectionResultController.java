package com.zyn.detection.controller;

import com.zyn.common.dto.response.DetectionResponse;
import com.zyn.common.dto.response.PageResponse;
import com.zyn.common.entity.DetectionResult;
import com.zyn.detection.dto.StatisticsResponse;
import com.zyn.detection.service.DetectionResultService;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 检测结果控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/detections")
@RequiredArgsConstructor
public class DetectionResultController {

    private final DetectionResultService detectionResultService;

    /**
     * 根据任务ID获取检测结果
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<DetectionResponse> getResultByTaskId(
            @PathVariable String taskId,
            Authentication authentication) {

        log.info("查询检测结果 - 任务ID: {}", taskId);

        Long userId = getUserIdFromAuth(authentication);
        DetectionResponse response = detectionResultService.getResultByTaskId(taskId, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 根据视频ID获取检测结果
     */
    @GetMapping("/video/{videoId}")
    public ResponseEntity<DetectionResponse> getResultByVideoId(
            @PathVariable Long videoId,
            Authentication authentication) {

        log.info("查询检测结果 - 视频ID: {}", videoId);

        // 暂时跳过认证检查，直接通过 service 查询
        DetectionResponse response = detectionResultService.getResultByVideoId(videoId, null);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取检测结果详情
     */
    @GetMapping("/{detectionId}")
    public ResponseEntity<DetectionResult> getDetectionDetail(
            @PathVariable Long detectionId,
            Authentication authentication) {

        log.info("查询检测详情 - 检测ID: {}", detectionId);

        Long userId = getUserIdFromAuth(authentication);
        DetectionResult result = detectionResultService.getDetectionDetail(detectionId, userId);

        return ResponseEntity.ok(result);
    }

    /**
     * 获取用户的检测历史
     */
    @GetMapping("/history")
    public ResponseEntity<PageResponse<DetectionResult>> getDetectionHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String result,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        log.info("查询检测历史 - 用户ID: {}, 页码: {}, 大小: {}, 结果: {}",
                userId, page, size, result);

        PageResponse<DetectionResult> response = detectionResultService
                .getDetectionHistory(userId, page, size, result);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户的统计数据
     */
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> getStatistics(
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        log.info("查询统计数据 - 用户ID: {}", userId);

        StatisticsResponse statistics = detectionResultService.getStatistics(userId);

        return ResponseEntity.ok(statistics);
    }

    /**
     * 从认证信息中获取用户ID
     */
    /**
     * 从认证信息中获取用户ID
     */
    private Long getUserIdFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("❌ 未认证的请求");
            throw new RuntimeException("未认证");
        }

        try {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> details = (java.util.Map<String, Object>) authentication.getDetails();

            if (details == null || !details.containsKey("userId")) {
                log.error("❌ Authentication details 中没有 userId");
                throw new RuntimeException("无法获取用户信息");
            }

            Object userIdObj = details.get("userId");
            if (userIdObj instanceof Integer) {
                return ((Integer) userIdObj).longValue();
            } else if (userIdObj instanceof Long) {
                return (Long) userIdObj;
            } else {
                 return Long.valueOf(userIdObj.toString());
            }

        } catch (Exception e) {
            log.error("❌ 无法从 Authentication 中提取 userId: {}", e.getMessage());
            throw new RuntimeException("认证信息格式错误");
        }
    }
}
