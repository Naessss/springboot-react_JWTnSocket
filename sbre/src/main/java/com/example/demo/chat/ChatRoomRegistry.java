package com.example.demo.chat;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

/**
 * 테스트용 인메모리 방 레지스트리.
 * - 방 생성 트리거: 누군가 구독하거나 메시지가 처음 도착할 때
 * - 참여자 카운트: SUBSCRIBE/UNSUBSCRIBE/DISCONNECT 이벤트로 세션 기준 관리
 * - 마지막 메시지/업데이트 시간: 메시지 도착 시 갱신
 */
@Component
public class ChatRoomRegistry {

    /** roomId -> RoomState */
    private final ConcurrentMap<String, RoomState> rooms = new ConcurrentHashMap<>();

    /** (sessionId::subscriptionId) -> roomId (UNSUBSCRIBE 처리용) */
    private final ConcurrentMap<String, String> subKeyToRoom = new ConcurrentHashMap<>();

    /** sessionId -> set(roomId) (DISCONNECT 시 정리용) */
    private final ConcurrentMap<String, Set<String>> sessionToRooms = new ConcurrentHashMap<>();

    /** 방 상태 내부 클래스 */
    private static class RoomState {
        final String roomId;
        final Instant createdAt;
        volatile Instant updatedAt;
        final Set<String> sessionIds = ConcurrentHashMap.newKeySet(); // 현재 접속 세션들
        volatile String lastMessagePreview;

        RoomState(String roomId) {
            this.roomId = roomId;
            this.createdAt = Instant.now();
            this.updatedAt = this.createdAt;
            this.lastMessagePreview = null;
        }

        ChatRoomSummary toSummary() {
            return new ChatRoomSummary(
                    roomId,
                    sessionIds.size(),
                    lastMessagePreview,
                    createdAt,
                    updatedAt
            );
        }
    }

    /** 방을 가져오거나 없으면 생성 */
    private RoomState getOrCreate(String roomId) {
        return rooms.computeIfAbsent(roomId, RoomState::new);
    }

    /** 메시지 도착 시(브로드캐스트 직전) 마지막 메시지/시간 갱신 */
    public void updateOnMessage(String roomId, String content) {
        RoomState rs = getOrCreate(roomId);
        rs.updatedAt = Instant.now();
        if (content == null) {
            rs.lastMessagePreview = null;
        } else {
            String trimmed = content.trim();
            rs.lastMessagePreview = trimmed.length() <= 50 ? trimmed : trimmed.substring(0, 50) + "…";
        }
    }

    /** 구독 시(세션 입장) */
    public void onSubscribe(String sessionId, String subscriptionId, String roomId) {
        RoomState rs = getOrCreate(roomId);
        rs.sessionIds.add(sessionId);
        rs.updatedAt = Instant.now();

        subKeyToRoom.put(key(sessionId, subscriptionId), roomId);
        sessionToRooms.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet()).add(roomId);
    }

    /** 구독 해제(세션에서 특정 구독만 해제) */
    public void onUnsubscribe(String sessionId, String subscriptionId) {
        String roomId = subKeyToRoom.remove(key(sessionId, subscriptionId));
        if (roomId == null) return;
        RoomState rs = rooms.get(roomId);
        if (rs != null) {
            rs.sessionIds.remove(sessionId);
            rs.updatedAt = Instant.now();
        }
        // session -> rooms 매핑도 정리
        Set<String> set = sessionToRooms.get(sessionId);
        if (set != null) {
            set.remove(roomId);
            if (set.isEmpty()) sessionToRooms.remove(sessionId);
        }
    }

    /** 세션 종료(브라우저 탭 닫힘 등) */
    public void onDisconnect(String sessionId) {
        Set<String> set = sessionToRooms.remove(sessionId);
        if (set != null) {
            for (String roomId : set) {
                RoomState rs = rooms.get(roomId);
                if (rs != null) {
                    rs.sessionIds.remove(sessionId);
                    rs.updatedAt = Instant.now();
                }
            }
        }
        // 해당 세션의 모든 subscription 키 제거
        subKeyToRoom.keySet().removeIf(k -> k.startsWith(sessionId + "::"));
    }

    /** 방 목록 조회(업데이트 시간 내림차순) */
    public List<ChatRoomSummary> listRooms() {
        return rooms.values().stream()
                .map(RoomState::toSummary)
                .sorted(Comparator.comparing(ChatRoomSummary::getUpdatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();
    }

    private static String key(String sessionId, String subscriptionId) {
        return sessionId + "::" + subscriptionId;
    }
}