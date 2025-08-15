// src/components/RoomsList.jsx
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

/**
 * props:
 *  - refreshMs (기본 3000ms)
 */
export default function RoomsList({ refreshMs = 3000 }) {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    try {
      const res = await fetch(`${import.meta.env.VITE_SERVER_URL}/chat/rooms`);
      
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      setRooms(data);
    } catch (e) {
      console.error("Failed to load rooms:", e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
    const t = setInterval(load, refreshMs);
    return () => clearInterval(t);
  }, [refreshMs]);

  if (loading) return <div>방 목록 로딩중…</div>;

  if (!rooms.length) {
    return <div style={{ color: "#666" }}>아직 생성된 방이 없어요. 상단에서 방에 들어가면 자동으로 생성됩니다.</div>;
  }

  return (
    <div style={{ display: "grid", gap: 8 }}>
      {rooms.map((r) => (
        <Link key={r.roomId} to={`/chat/${encodeURIComponent(r.roomId)}`}
              style={{ display: "block", padding: 12, border: "1px solid #ddd", borderRadius: 8, textDecoration: "none", color: "inherit" }}>
          <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 6 }}>
            <strong>#{r.roomId}</strong>
            <span>👥 {r.memberCount}</span>
          </div>
          {r.lastMessagePreview && (
            <div style={{ color: "#444" }}>{r.lastMessagePreview}</div>
          )}
          <div style={{ color: "#888", fontSize: 12, marginTop: 4 }}>
            업데이트: {r.updatedAt ?? r.createdAt}
          </div>
        </Link>
      ))}
    </div>
  );
}

