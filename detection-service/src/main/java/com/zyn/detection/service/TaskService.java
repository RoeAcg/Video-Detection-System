package com.zyn.detection.service;

import com.zyn.common.dto.response.PageResponse;
import com.zyn.common.dto.response.TaskStatusResponse;
import com.zyn.common.entity.DetectionTask;

/**
 * 任务服务接口
 */
public interface TaskService {

    /**
     * 获取任务状态
     */
    TaskStatusResponse getTaskStatus(String taskId, Long userId);

    /**
     * 获取任务详情
     */
    DetectionTask getTaskDetail(String taskId, Long userId);

    /**
     * 获取任务列表
     */
    PageResponse<DetectionTask> getMyTasks(Long userId, int page, int size, String status);

    /**
     * 取消任务
     */
    void cancelTask(String taskId, Long userId);
}
