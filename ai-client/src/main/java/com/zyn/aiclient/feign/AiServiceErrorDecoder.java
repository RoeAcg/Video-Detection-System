package com.zyn.aiclient.feign;

import com.zyn.aiclient.exception.AiServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * AI服务错误解码器
 * 将HTTP错误响应转换为自定义异常
 */
@Slf4j
public class AiServiceErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        String requestUrl = response.request().url();

        log.error("AI服务调用失败 - 方法: {}, URL: {}, 状态码: {}",
                methodKey, requestUrl, status);

        switch (status) {
            case 400:
                return AiServiceException.invalidRequest("请求参数错误");
            case 408:
                return AiServiceException.timeout();
            case 500:
                return AiServiceException.processingFailed("服务器内部错误");
            case 503:
                return AiServiceException.serviceUnavailable();
            default:
                return defaultDecoder.decode(methodKey, response);
        }
    }
}
