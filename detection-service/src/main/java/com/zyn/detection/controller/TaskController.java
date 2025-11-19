package com.zyn.detection.controller;

import com.zyn.common.dto.response.PageResponse;
import com.zyn.common.dto.response.TaskStatusResponse;
import com.zyn.common.entity.DetectionTask;
import com.zyn.detection.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 检测任务控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * 获取任务状态
     */
    @GetMapping("/{taskId}/status")
    public ResponseEntity<TaskStatusResponse> getTaskStatus(
            @PathVariable String taskId,
            Authentication authentication) {

        log.info("查询任务状态 - 任务ID: {}", taskId);

        Long userId = getUserIdFromAuth(authentication);
        TaskStatusResponse response = taskService.getTaskStatus(taskId, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<DetectionTask> getTaskDetail(
            @PathVariable String taskId,
            Authentication authentication) {

        log.info("查询任务详情 - 任务ID: {}", taskId);

        Long userId = getUserIdFromAuth(authentication);
        DetectionTask task = taskService.getTaskDetail(taskId, userId);

        return ResponseEntity.ok(task);
    }

    /**
     * 获取用户的任务列表
     */
    @GetMapping("/my")
    public ResponseEntity<PageResponse<DetectionTask>> getMyTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        log.info("查询任务列表 - 用户ID: {}, 页码: {}, 大小: {}, 状态: {}",
                userId, page, size, status);

        PageResponse<DetectionTask> response = taskService.getMyTasks(userId, page, size, status);

        return ResponseEntity.ok(response);
    }

    /**
     * 取消任务
     */
    @PostMapping("/{taskId}/cancel")
    public ResponseEntity<String> cancelTask(
            @PathVariable String taskId,
            Authentication authentication) {

        log.info("取消任务 - 任务ID: {}", taskId);

        Long userId = getUserIdFromAuth(authentication);
        taskService.cancelTask(taskId, userId);

        return ResponseEntity.ok("任务已取消");
    }

    /**
     * 从认证信息中获取用户ID
     */
    private Long getUserIdFromAuth(Authentication authentication) {
        return 1L; // TODO: 从JWT token中获取真实用户ID
    }
}
