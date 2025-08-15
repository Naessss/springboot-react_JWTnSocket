package com.example.demo.chat;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 클라 SEND → /app/chat.send
 * 서버 브로드캐스트 → /topic/room.{roomId}
 */
@Controller
public class ChatController {

    private final SimpMessagingTemplate template;

    public ChatController(SimpMessagingTemplate template) {
        this.template = template;
    }
    
    @Autowired
    private ChatRoomRegistry registry;

    @MessageMapping("/chat.send")
    public void send(@Payload ChatMessage msg, Authentication auth) {
        // auth가 null이면 CONNECT 인터셉터에서 setUser 누락
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("Unauthenticated STOMP message");
        }

        // 최소 유효성
        if (msg.getRoomId() == null || msg.getRoomId().isBlank()) {
            throw new IllegalArgumentException("roomId is required");
        }
        if (msg.getContent() == null) msg.setContent("");

        // 서버 신뢰 필드 채움
        msg.setType(msg.getType() == null ? ChatMessage.Type.CHAT : msg.getType());
        msg.setSender(auth.getName());
        msg.setTimestamp(Instant.now());
        
     // 방 메타 갱신(마지막 메시지/업데이트 시각)
        registry.updateOnMessage(msg.getRoomId(), msg.getContent());

        // 브로드캐스트
        String dest = "/topic/room." + msg.getRoomId();
        template.convertAndSend(dest, msg);
    }
    
    @GetMapping("/chat/rooms")
    @ResponseBody
    public List<ChatRoomSummary> listRooms() {
        return registry.listRooms();
    }
}
