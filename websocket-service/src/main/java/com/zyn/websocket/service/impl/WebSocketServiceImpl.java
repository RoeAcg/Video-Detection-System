package com.zyn.websocket.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyn.websocket.dto.WebSocketMessage;
import com.zyn.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket服务实现
 */
@Slf4j
@Service
public class WebSocketServiceImpl implements WebSocketService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 用户ID -> 会话集合（一个用户可能有多个连接）
    private final Map<String, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    @Override
    public void registerSession(String userId, WebSocketSession session) {
        userSessions.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>())
                .add(session);

        log.info("会话已注册 - 用户ID: {}, 会话ID: {}, 当前连接数: {}",
                userId, session.getId(), userSessions.get(userId).size());
    }

    @Override
    public void removeSession(String userId, String sessionId) {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.removeIf(session -> session.getId().equals(sessionId));

            // 如果用户没有任何连接了，移除用户
            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }

            log.info("会话已移除 - 用户ID: {}, 会话ID: {}", userId, sessionId);
        }
    }

    @Override
    public void sendToUser(String userId, WebSocketMessage message) {
        Set<WebSocketSession> sessions = userSessions.get(userId);

        if (sessions == null || sessions.isEmpty()) {
            log.warn("用户不在线，无法发送消息 - 用户ID: {}", userId);
            return;
        }

        String json;
        try {
            json = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            log.error("序列化消息失败: {}", e.getMessage());
            return;
        }

        int successCount = 0;
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(json));
                    successCount++;
                } catch (IOException e) {
                    log.error("发送消息失败 - 会话ID: {}, 错误: {}",
                            session.getId(), e.getMessage());
                    // 移除失败的会话
                    sessions.remove(session);
                }
            } else {
                // 移除已关闭的会话
                sessions.remove(session);
            }
        }

        log.debug("消息已发送 - 用户ID: {}, 成功: {}/{}",
                userId, successCount, sessions.size());
    }

    @Override
    public void broadcast(WebSocketMessage message) {
        String json;
        try {
            json = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            log.error("序列化消息失败: {}", e.getMessage());
            return;
        }

        int totalSessions = 0;
        int successCount = 0;

        for (Map.Entry<String, Set<WebSocketSession>> entry : userSessions.entrySet()) {
            for (WebSocketSession session : entry.getValue()) {
                totalSessions++;
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(json));
                        successCount++;
                    } catch (IOException e) {
                        log.error("广播消息失败 - 会话ID: {}, 错误: {}",
                                session.getId(), e.getMessage());
                    }
                }
            }
        }

        log.info("广播消息完成 - 总会话数: {}, 成功: {}", totalSessions, successCount);
    }

    @Override
    public int getOnlineUserCount() {
        return userSessions.size();
    }
}
