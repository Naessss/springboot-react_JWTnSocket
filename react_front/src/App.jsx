import { useEffect, useState } from 'react'
import './App.css'
import Header from './Header';
import Login from './Login';
import Signup from './Singup';
import { Link, Route, Routes } from 'react-router-dom';
import axiosInstance from './axiosInstance';
import WriteBoard from './WriteBoard';
import BoardList from './BoardList';
import BoardDetail from './BoardDetail';
import LoginMsg from './LoginMsg';
import Lobby from './chat/Lobby';
import ChatRoomRoute from './chat/ChatRoomRoute';

function App() {
  const [isAuth, setIsAuth] = useState(false);
  const [userInfo, setUserInfo] = useState();

  useEffect(() => {
    if(sessionStorage.getItem('jwt'))
      setIsAuth(true)
  }, [])

  useEffect(() => {
    if(isAuth) {
      axiosInstance.get('/userInfo')
        .then(response => {
          console.log(response.data);
          setUserInfo(response.data);
        }).catch(error => {
          console.log(error);
        })
    }
  }, [isAuth])

  return (
    <div>
      <Header isAuth={isAuth} setIsAuth={setIsAuth} />
      <Link to="/lobby">채팅로비</Link>
      <Routes>
        <Route path='/' element={<BoardList />} />
        <Route path='/login' element={<Login setIsAuth={setIsAuth} />} />
        <Route path='/signup' element={<Signup />} />
        <Route path='/write' element={<WriteBoard userInfo={userInfo} />} />
        <Route path='/board/:id' element={isAuth ? 
																	<BoardDetail userInfo={userInfo} isAuth={isAuth} />
																	 : <LoginMsg />}/>
        <Route path="/lobby" element={<Lobby />} />
        {/* /chat/:roomId 로 진입하면 ChatRoomRoute가 roomId를 읽어 ChatRoom에 전달 */}
        <Route path="/chat/:roomId" element={<ChatRoomRoute jwt={sessionStorage.getItem('jwt')} />} />
      </Routes>
    </div>
  )
}

export default App
