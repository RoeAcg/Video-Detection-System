package com.zyn.aiclient.feign;

import com.zyn.aiclient.dto.AiDetectionRequest;
import com.zyn.aiclient.dto.AiDetectionResponse;
import com.zyn.aiclient.dto.AiModelInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * AI服务Feign客户端接口
 *
 */
@FeignClient(
        name = "ai-service",
        url = "${ai.service.url:http://localhost:5000}",
        fallback = AiServiceFallback.class,
        configuration = FeignConfig.class
)
public interface AiServiceClient {

    /**
     * 提交视频/图片检测任务
     */
    @PostMapping("/api/detect")
    AiDetectionResponse detect(@RequestBody AiDetectionRequest request);

    /**
     * 获取模型信息 (暂未对应API，保留或移除，这里暂时移除以匹配文档)
     */
    // @GetMapping("/api/v1/model/info")
    // AiModelInfo getModelInfo();

    /**
     * 健康检查
     */
    @GetMapping("/health")
    String healthCheck();
}

