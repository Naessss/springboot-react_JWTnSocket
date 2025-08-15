import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axiosInstance from "./axiosInstance";

function BoardDetail({ userInfo }) {
  const {id} = useParams();
  const [board, setBoard] = useState();
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    axiosInstance.get('/board/' + id)
    .then(response => {
      console.log(response.data);
      setBoard(response.data);
    }).catch(error => {
      console.log(error);
    }).finally(() => {
      setIsLoading(false);
    })

  }, [])

  if(isLoading)
    return <div>로딩중입니다.</div>

  if(!board)
    return <div>존재하지 않는 게시물입니다.</div>

  return (
    <>
      <div>제목 : {board.title}</div>
      <div>작성자 : {board.writer.username}</div>
      <div>내용 : {board.content}</div>

      <button onClick={() => {
        if(board.writer.username != userInfo.username) {
          alert('작성자만 삭제가능합니다.')
          return;
        }

        axiosInstance.delete('/board', {params : {"id" : board.id}})
        .then(response => {
          alert(response.data);
          navigate('/');
        }).catch(error => {
          console.log(error);
        })
      }}>게시물 삭제</button>
    </>
  );
}

export default BoardDetail;