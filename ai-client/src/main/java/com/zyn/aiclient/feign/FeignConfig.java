package com.zyn.aiclient.feign;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign客户端配置
 * Spring Cloud 2025.0.0 + Spring Boot 3.5.7
 */
@Slf4j
@Configuration
public class FeignConfig {

    @Value("${ai.client.connect-timeout:10000}")
    private int connectTimeout;

    @Value("${ai.client.read-timeout:120000}")
    private int readTimeout;

    /**
     * 配置Feign日志级别
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        log.info("配置 Feign 日志级别: BASIC");
        return Logger.Level.BASIC;
    }

    /**
     * 配置超时时间
     * 从配置文件读取，提供默认值
     */
    @Bean
    public Request.Options requestOptions() {
        log.info("配置 Feign 超时 - 连接: {}ms, 读取: {}ms",
                connectTimeout, readTimeout);
        return new Request.Options(
                connectTimeout,     // 连接超时（毫秒）
                readTimeout         // 读取超时（毫秒）
        );
    }

    /**
     * 配置重试策略
     */
    @Bean
    public Retryer feignRetryer() {
        log.info("配置 Feign 重试策略 - 最大3次，间隔1-3秒");
        return new Retryer.Default(
                1000L,      // 初始重试间隔
                3000L,      // 最大重试间隔
                3           // 最大重试次数
        );
    }

    /**
     * 自定义错误解码器
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        log.info("配置 Feign 自定义错误解码器");
        return new AiServiceErrorDecoder();
    }
}
