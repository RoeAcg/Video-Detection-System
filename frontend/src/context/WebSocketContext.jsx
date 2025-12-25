import React, { createContext, useContext, useEffect, useRef, useState } from 'react';

const WebSocketContext = createContext(null);

export const WebSocketProvider = ({ children }) => {
    const [socket, setSocket] = useState(null);
    const [isConnected, setIsConnected] = useState(false);
    const [lastMessage, setLastMessage] = useState(null);
    const reconnectTimeoutRef = useRef(null);

    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const userId = user.id;

    const connect = () => {
        if (!userId) return;

        // WebSocket Service runs on port 8195
        const wsUrl = `ws://localhost:8195/ws/notifications?userId=${userId}`;
        const ws = new WebSocket(wsUrl);

        ws.onopen = () => {
            console.log('WebSocket Connected');
            setIsConnected(true);
            // Send PING periodically
            startHeartbeat(ws);
        };

        ws.onmessage = (event) => {
            try {
                const message = JSON.parse(event.data);
                console.log('WebSocket Message:', message);

                if (message.type === 'PONG') {
                    // Heartbeat response, ignore
                } else {
                    setLastMessage(message);
                }
            } catch (e) {
                console.error('Failed to parse WebSocket message:', e);
            }
        };

        ws.onclose = () => {
            console.log('WebSocket Disconnected');
            setIsConnected(false);
            setSocket(null);

            // Try to reconnect
            if (reconnectTimeoutRef.current) clearTimeout(reconnectTimeoutRef.current);
            reconnectTimeoutRef.current = setTimeout(() => {
                console.log('Attempting to reconnect...');
                connect();
            }, 5000);
        };

        ws.onerror = (error) => {
            console.error('WebSocket Error:', error);
            ws.close();
        };

        setSocket(ws);
    };

    const startHeartbeat = (ws) => {
        setInterval(() => {
            if (ws.readyState === WebSocket.OPEN) {
                ws.send(JSON.stringify({ type: 'PING' }));
            }
        }, 30000);
    };

    useEffect(() => {
        if (userId) {
            connect();
        }

        return () => {
            if (socket) {
                socket.close();
            }
            if (reconnectTimeoutRef.current) {
                clearTimeout(reconnectTimeoutRef.current);
            }
        };
    }, [userId]);

    return (
        <WebSocketContext.Provider value={{ socket, isConnected, lastMessage }}>
            {children}
        </WebSocketContext.Provider>
    );
};

export const useWebSocket = () => useContext(WebSocketContext);
