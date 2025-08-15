import { useEffect, useState } from "react";
import axiosInstance from "./axiosInstance";
import { Link } from "react-router-dom";

function BoardList() {
  const [boardList, setBoardList] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    axiosInstance.get('/board')
    .then(response => {
      console.log(response.data);
      setBoardList(response.data);
      setIsLoading(false);
    }).catch(error => {
      console.log(error);
    })
  }, [])

  if(isLoading)
    return <div>로딩중입니다.</div>

  return(
      <>
        <table>
          <thead>
            <tr>
              <th>번호</th>
              <th>제목</th>
              <th>작성자</th>
            </tr>
          </thead>
          <tbody>
            {
              boardList.map((board, i) => {
                return (
                  <tr key={i}>
                    <td>{board.id}</td>
                    <td>
                      <Link to={`/board/${board.id}`}>{board.title}</Link>
                    </td>
                    <td>{board.writer.username}</td>
                  </tr>
                );
              })
            }
          </tbody>
        </table>
      </>
  );
}

export default BoardList;