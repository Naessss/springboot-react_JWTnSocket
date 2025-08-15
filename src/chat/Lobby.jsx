// src/pages/Lobby.jsx
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import RoomsList from "./RoomList";

export default function Lobby() {
  const [roomId, setRoomId] = useState("");
  const navigate = useNavigate();

  const go = () => {
    const id = roomId.trim();
    if (id) navigate(`/chat/${encodeURIComponent(id)}`);
  };

  return (
    <div>
      <h2>채팅 로비</h2>
      <p style={{ color: "#666" }}>
        방 ID를 입력하거나 샘플 링크를 눌러 입장해 보세요.
      </p>

      <div style={{ display: "flex", gap: 8, margin: "12px 0 20px" }}>
        <input
          style={{ flex: 1 }}
          placeholder="예: demo"
          value={roomId}
          onChange={(e) => setRoomId(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && go()}
        />
        <button onClick={go} disabled={!roomId.trim()}>
          입장
        </button>
      </div>

      <RoomsList />

      <div style={{ display: "grid", gap: 8 }}>
        <Link to="/chat/demo">🔗 demo 방</Link>
        <Link to={`/chat/${Date.now().toString(36)}`}>🔗 랜덤 방</Link>
      </div>
    </div>
  );
}
