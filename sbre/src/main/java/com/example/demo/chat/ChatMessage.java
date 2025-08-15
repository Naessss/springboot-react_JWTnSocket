package com.example.demo.chat;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

/**
 * 클라이언트가 보내는 최소 페이로드: roomId, content
 * 서버가 채움: sender, timestamp, (type 기본: CHAT)
 */
@Getter
@Setter
public class ChatMessage {
    public enum Type { CHAT, ENTER, LEAVE }

    private String roomId;
    private String content;
    private Type type;

    // 서버 채움
    private String sender;
    private Instant timestamp;
}
