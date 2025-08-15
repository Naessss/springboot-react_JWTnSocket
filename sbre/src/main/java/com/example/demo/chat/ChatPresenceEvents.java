package com.example.demo.chat;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

/**
 * STOMP 구독/해제/연결 종료 이벤트를 받아 방 참여자 수를 관리.
 * - 구독 목적지: /topic/room.{roomId}
 */
@Component
public class ChatPresenceEvents {

    private static final String ROOM_PREFIX = "/topic/room.";

    private final ChatRoomRegistry registry;

    public ChatPresenceEvents(ChatRoomRegistry registry) {
        this.registry = registry;
    }

    @EventListener
    public void onSubscribe(SessionSubscribeEvent e) {
        var acc = SimpMessageHeaderAccessor.wrap(e.getMessage());
        String dest = acc.getDestination();          // 구독 destination
        String sessionId = acc.getSessionId();       // STOMP 세션 ID
        String subId = acc.getSubscriptionId();      // 구독 ID

        if (dest != null && dest.startsWith(ROOM_PREFIX) && sessionId != null && subId != null) {
            String roomId = dest.substring(ROOM_PREFIX.length());
            registry.onSubscribe(sessionId, subId, roomId);
        }
    }

    @EventListener
    public void onUnsubscribe(SessionUnsubscribeEvent e) {
        var acc = SimpMessageHeaderAccessor.wrap(e.getMessage());
        String sessionId = acc.getSessionId();
        String subId = acc.getSubscriptionId();
        if (sessionId != null && subId != null) {
            registry.onUnsubscribe(sessionId, subId);
        }
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent e) {
        var acc = SimpMessageHeaderAccessor.wrap(e.getMessage());
        String sessionId = acc.getSessionId();
        if (sessionId != null) {
            registry.onDisconnect(sessionId);
        }
    }
}
