package com.example.demo.chat;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomSummary {
    private String roomId;
    private int memberCount;
    private String lastMessagePreview; // 최대 50자 내 트림
    private Instant createdAt;
    private Instant updatedAt;
}
