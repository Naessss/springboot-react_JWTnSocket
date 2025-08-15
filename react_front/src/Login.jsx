import axios from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

function Login({setIsAuth}) {
  const navigate = useNavigate();
  const [member, setMember] = useState({
    username : '',
    password : ''
  });

  const handleChange = (e) => {
    setMember({
      ...member,
      [e.target.name] : e.target.value
    })
  }

  return (
    <div>
      <h1>로그인 페이지</h1>
      아이디 : <input type="text" name="username" onChange={handleChange}/> <br/>
      비밀번호 : <input type="text" name="password" onChange={handleChange}/> <br/>
      <button onClick={() => {
        axios.post(`${import.meta.env.VITE_SERVER_URL}/login`, member)
        .then((res) => {
          const jwt = res.headers.authorization;

          if(jwt) {
            sessionStorage.setItem('jwt', jwt);
            setIsAuth(true);
            navigate('/');
          }
        })
        .catch((err) => {
          alert('로그인 실패');
          console.log(err);
        })
      }}>로그인</button>
    </div>
  );
}

export default Login;