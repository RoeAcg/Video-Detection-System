package com.zyn.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyn.websocket.dto.WebSocketMessage;
import com.zyn.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

/**
 * 通知WebSocket处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketService webSocketService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 连接建立后
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserIdFromSession(session);
        log.info("WebSocket连接建立 - 会话ID: {}, 用户ID: {}", session.getId(), userId);

        // 注册会话
        webSocketService.registerSession(userId, session);

        // 发送欢迎消息
        WebSocketMessage welcomeMessage = WebSocketMessage.builder()
                .type("CONNECTED")
                .message("连接成功")
                .timestamp(System.currentTimeMillis())
                .build();

        sendMessage(session, welcomeMessage);
    }

    /**
     * 接收到消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.debug("收到WebSocket消息 - 会话ID: {}, 内容: {}", session.getId(), payload);

        try {
            // 解析消息
            WebSocketMessage wsMessage = objectMapper.readValue(payload, WebSocketMessage.class);

            // 处理心跳
            if ("PING".equals(wsMessage.getType())) {
                WebSocketMessage pongMessage = WebSocketMessage.builder()
                        .type("PONG")
                        .timestamp(System.currentTimeMillis())
                        .build();
                sendMessage(session, pongMessage);
            }

        } catch (Exception e) {
            log.error("处理WebSocket消息失败 - 会话ID: {}, 错误: {}",
                    session.getId(), e.getMessage());
        }
    }

    /**
     * 连接关闭后
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserIdFromSession(session);
        log.info("WebSocket连接关闭 - 会话ID: {}, 用户ID: {}, 状态: {}",
                session.getId(), userId, status);

        // 移除会话
        webSocketService.removeSession(userId, session.getId());
    }

    /**
     * 传输错误
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误 - 会话ID: {}, 错误: {}",
                session.getId(), exception.getMessage(), exception);

        // 关闭会话
        if (session.isOpen()) {
            session.close();
        }
    }

    /**
     * 从会话中获取用户ID
     */
    private String getUserIdFromSession(WebSocketSession session) {
        // 从查询参数中获取用户ID
        // 例如: ws://localhost:8085/ws/notifications?userId=123
        String query = session.getUri().getQuery();
        if (query != null && query.contains("userId=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("userId=")) {
                    return param.substring("userId=".length());
                }
            }
        }
        return "unknown";
    }

    /**
     * 发送消息
     */
    private void sendMessage(WebSocketSession session, WebSocketMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            log.error("发送WebSocket消息失败 - 会话ID: {}, 错误: {}",
                    session.getId(), e.getMessage());
        }
    }
}
