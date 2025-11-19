package com.zyn.websocket.service;

import com.zyn.websocket.dto.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * WebSocket服务接口
 */
public interface WebSocketService {

    /**
     * 注册会话
     */
    void registerSession(String userId, WebSocketSession session);

    /**
     * 移除会话
     */
    void removeSession(String userId, String sessionId);

    /**
     * 发送消息给指定用户
     */
    void sendToUser(String userId, WebSocketMessage message);

    /**
     * 广播消息给所有用户
     */
    void broadcast(WebSocketMessage message);

    /**
     * 获取在线用户数
     */
    int getOnlineUserCount();
}
