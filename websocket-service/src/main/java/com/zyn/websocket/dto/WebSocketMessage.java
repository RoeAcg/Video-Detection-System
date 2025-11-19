package com.zyn.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * WebSocket消息DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebSocketMessage {

    /**
     * 消息类型
     * CONNECTED - 连接成功
     * PING/PONG - 心跳
     * DETECTION_COMPLETED - 检测完成
     * TASK_PROGRESS - 任务进度
     * ERROR - 错误消息
     */
    private String type;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 附加数据
     */
    private Map<String, Object> data;

    /**
     * 时间戳
     */
    private Long timestamp;
}
