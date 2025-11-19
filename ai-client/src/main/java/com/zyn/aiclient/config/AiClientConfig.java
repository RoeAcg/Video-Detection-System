package com.zyn.aiclient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * AI客户端配置属性
 * 从application.yml读取配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ai.client")
public class AiClientConfig {

    /**
     * AI服务基础URL
     */
    private String baseUrl = "http://localhost:5000";

    /**
     * 连接超时（秒）
     */
    private Integer connectTimeout = 10;

    /**
     * 读取超时（秒）
     */
    private Integer readTimeout = 120;

    /**
     * 最大重试次数
     */
    private Integer maxRetries = 3;

    /**
     * 重试间隔（毫秒）
     */
    private Long retryInterval = 1000L;

    /**
     * 是否启用降级
     */
    private Boolean fallbackEnabled = true;

    /**
     * 默认检测模式
     */
    private String defaultMode = "standard";

    /**
     * 默认采样帧率
     */
    private Integer defaultFrameRate = 5;

    /**
     * 最大并发请求数
     */
    private Integer maxConcurrentRequests = 10;
}
