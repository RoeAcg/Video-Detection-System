package com.zyn.worker.service;

import com.zyn.common.dto.event.DetectionTaskEvent;

/**
 * 检测处理服务接口
 */
public interface DetectionProcessor {

    /**
     * 处理检测任务
     */
    void processDetectionTask(DetectionTaskEvent event);
}