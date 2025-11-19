package com.zyn.aiclient.feign;

import com.zyn.aiclient.dto.AiDetectionRequest;
import com.zyn.aiclient.dto.AiDetectionResponse;
import com.zyn.aiclient.dto.AiModelInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * AI服务降级处理
 */
@Slf4j
@Component
public class AiServiceFallback implements AiServiceClient {

    @Override
    public AiDetectionResponse detect(AiDetectionRequest request) {
        log.error("AI服务调用失败，执行降级逻辑。任务ID: {}", request.getTaskId());

        return AiDetectionResponse.builder()
                .taskId(request.getTaskId())
                .result("UNCERTAIN")
                .confidence(BigDecimal.valueOf(0.0))
                .success(false)
                .errorMessage("AI服务暂时不可用，请稍后重试")
                .processingTimeMs(0L)
                .framesAnalyzed(0)
                .build();
    }

    @Override
    public AiModelInfo getModelInfo() {
        log.warn("AI服务不可用，无法获取模型信息");

        return AiModelInfo.builder()
                .modelName("unknown")
                .version("N/A")
                .available(false)
                .description("AI服务暂时不可用")
                .build();
    }

    @Override
    public String healthCheck() {
        log.warn("AI服务健康检查失败");
        return "AI service is unavailable";
    }
}
