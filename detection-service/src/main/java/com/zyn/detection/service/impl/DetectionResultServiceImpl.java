package com.zyn.detection.service.impl;

import com.zyn.common.constant.CacheConstants;
import com.zyn.common.dto.response.DetectionResponse;
import com.zyn.common.dto.response.PageResponse;
import com.zyn.common.entity.DetectionResult;
import com.zyn.common.enums.DetectionResultEnum;
import com.zyn.common.exception.ForbiddenException;
import com.zyn.common.exception.ResourceNotFoundException;
import com.zyn.detection.dto.StatisticsResponse;
import com.zyn.detection.repository.DetectionResultRepository;
import com.zyn.detection.service.DetectionResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 检测结果服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DetectionResultServiceImpl implements DetectionResultService {

    private final DetectionResultRepository resultRepository;

    @Override
    @Cacheable(value = "detectionResults", key = "#taskId")
    public DetectionResponse getResultByTaskId(String taskId, Long userId) {
        DetectionResult result = resultRepository.findByTaskId(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("检测结果", taskId));

        // 权限检查
        if (!result.getUserId().equals(userId)) {
            throw ForbiddenException.resourceAccessDenied("检测结果");
        }

        return convertToResponse(result);
    }

    @Override
    @Cacheable(value = "detectionResults", key = "'video:' + #videoId")
    public DetectionResponse getResultByVideoId(Long videoId, Long userId) {
        DetectionResult result = resultRepository.findByVideoId(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("检测结果", videoId));

        // 权限检查
        if (!result.getUserId().equals(userId)) {
            throw ForbiddenException.resourceAccessDenied("检测结果");
        }

        return convertToResponse(result);
    }

    @Override
    public DetectionResult getDetectionDetail(Long detectionId, Long userId) {
        DetectionResult result = resultRepository.findById(detectionId)
                .orElseThrow(() -> new ResourceNotFoundException("检测结果", detectionId));

        // 权限检查
        if (!result.getUserId().equals(userId)) {
            throw ForbiddenException.resourceAccessDenied("检测结果");
        }

        return result;
    }

    @Override
    public PageResponse<DetectionResult> getDetectionHistory(
            Long userId, int page, int size, String result) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<DetectionResult> resultPage;

        if (result != null && !result.isEmpty()) {
            DetectionResultEnum resultEnum = DetectionResultEnum.valueOf(result.toUpperCase());
            resultPage = resultRepository.findByUserIdAndPrediction(userId, resultEnum, pageable);
        } else {
            resultPage = resultRepository.findByUserId(userId, pageable);
        }

        return PageResponse.<DetectionResult>builder()
                .content(resultPage.getContent())
                .page(page)
                .size(size)
                .totalPages(resultPage.getTotalPages())
                .totalElements(resultPage.getTotalElements())
                .first(resultPage.isFirst())
                .last(resultPage.isLast())
                .build();
    }

    @Override
    public StatisticsResponse getStatistics(Long userId) {
        // 总检测数
        long totalDetections = resultRepository.countByUserId(userId);

        // 真实视频数
        long authenticCount = resultRepository.countByUserIdAndPrediction(
                userId, DetectionResultEnum.AUTHENTIC);

        // 伪造视频数
        long fakeCount = resultRepository.countByUserIdAndPrediction(
                userId, DetectionResultEnum.FAKE);

        // 不确定数
        long uncertainCount = resultRepository.countByUserIdAndPrediction(
                userId, DetectionResultEnum.UNCERTAIN);

        // 平均置信度
        Double avgConfidence = resultRepository.calculateAverageConfidence(userId);

        return StatisticsResponse.builder()
                .totalDetections(totalDetections)
                .authenticCount(authenticCount)
                .fakeCount(fakeCount)
                .uncertainCount(uncertainCount)
                .averageConfidence(avgConfidence != null ? avgConfidence : 0.0)
                .build();
    }

    /**
     * 转换为响应DTO
     */
    private DetectionResponse convertToResponse(DetectionResult result) {
        return DetectionResponse.builder()
                .detectionId(result.getId())
                .videoId(result.getVideoId())
                .fileName(null) // TODO: 从Video表获取文件名
                .result(result.getPrediction())
                .confidence(result.getConfidence())
                .modelVersion(result.getModelVersion())
                .processingTimeMs(result.getProcessingTimeMs())
                .framesAnalyzed(result.getFramesAnalyzed())
                .artifacts(result.getArtifactsDetected() != null
                        ? Arrays.asList(result.getArtifactsDetected().split(","))
                        : null)
                .features(result.getFeatures())
                .createdAt(result.getCreatedAt())
                .build();
    }
}
