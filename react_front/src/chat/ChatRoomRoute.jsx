// src/pages/ChatRoomRoute.jsx
import { useParams } from "react-router-dom";
import ChatRoom from "./ChatRoom";

export default function ChatRoomRoute({ jwt }) {
  const { roomId } = useParams();

  // roomId가 없을 때의 가드(이례적)
  if (!roomId) return <div>잘못된 경로입니다.</div>;

  return (
    <ChatRoom
      jwt={jwt}
      roomId={roomId}
    />
  );
}
