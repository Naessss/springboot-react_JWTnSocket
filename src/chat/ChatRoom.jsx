// src/ChatRoom.jsx
import React, { useEffect, useRef, useState } from "react";
import { Client } from "@stomp/stompjs";
// import SockJS from "sockjs-client"; // 필요 시 사용

/**
 * props:
 *  - serverUrl: "ws://localhost:8080/ws" 또는 "wss://example.com/ws"
 *  - jwt: JWT 문자열
 *  - roomId: 방 아이디
 */
export default function ChatRoom({ jwt, roomId }) {
  const clientRef = useRef(null);
  const [connected, setConnected] = useState(false);
  const [messages, setMessages] = useState([]);
  const [content, setContent] = useState("");
  const serverUrl = `ws://localhost:8888/ws`;

  useEffect(() => {
    const client = new Client({
      // SockJS 쓰면 아래 대신 webSocketFactory 사용:
      // webSocketFactory: () => new SockJS(serverUrl.replace(/^ws/, "http")),
      brokerURL: serverUrl,
      connectHeaders: {
        // STOMP CONNECT 헤더로 Authorization 전송 → 서버 인터셉터가 읽음
        Authorization: `${jwt}`,
      },
      debug: (str) => console.log("[STOMP]", str),
      reconnectDelay: 2000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      onConnect: () => {
        setConnected(true);
        client.subscribe(`/topic/room.${roomId}`, (frame) => {
          try {
            const body = JSON.parse(frame.body);
            setMessages((prev) => [...prev, body]);
          } catch {}
        });
      },
      onWebSocketClose: () => setConnected(false),
      onStompError: (frame) => {
        console.error("Broker error:", frame.headers["message"], frame.body);
      },
    });

    client.activate();
    clientRef.current = client;
    return () => client.deactivate();
  }, [serverUrl, jwt, roomId]);

  const sendMessage = () => {
    if (!clientRef.current || !connected) return;
    clientRef.current.publish({
      destination: "/app/chat.send",
      body: JSON.stringify({ roomId, content }),
    });
    setContent("");
  };

  return (
    <div style={{ maxWidth: 520, margin: "0 auto" }}>
      <h3>Room: {roomId} {connected ? "🟢" : "🔴"}</h3>

      <div style={{ height: 260, border: "1px solid #ccc", borderRadius: 8, padding: 8, overflowY: "auto", marginBottom: 8 }}>
        {messages.map((m, i) => (
          <div key={i} style={{ marginBottom: 6 }}>
            <b>{m.sender}</b> <small>{m.timestamp}</small>
            <div>{m.content}</div>
          </div>
        ))}
      </div>

      <div style={{ display: "flex", gap: 8 }}>
        <input
          style={{ flex: 1 }}
          placeholder="메시지를 입력하세요"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && sendMessage()}
        />
        <button onClick={sendMessage} disabled={!connected || !content.trim()}>
          전송
        </button>
      </div>
    </div>
  );
}
