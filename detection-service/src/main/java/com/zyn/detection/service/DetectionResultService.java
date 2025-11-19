package com.zyn.detection.service;

import com.zyn.common.dto.response.DetectionResponse;
import com.zyn.common.dto.response.PageResponse;
import com.zyn.common.entity.DetectionResult;
import com.zyn.detection.dto.StatisticsResponse;

/**
 * 检测结果服务接口
 */
public interface DetectionResultService {

    /**
     * 根据任务ID获取检测结果
     */
    DetectionResponse getResultByTaskId(String taskId, Long userId);

    /**
     * 根据视频ID获取检测结果
     */
    DetectionResponse getResultByVideoId(Long videoId, Long userId);

    /**
     * 获取检测结果详情
     */
    DetectionResult getDetectionDetail(Long detectionId, Long userId);

    /**
     * 获取检测历史
     */
    PageResponse<DetectionResult> getDetectionHistory(Long userId, int page, int size, String result);

    /**
     * 获取统计数据
     */
    StatisticsResponse getStatistics(Long userId);
}
