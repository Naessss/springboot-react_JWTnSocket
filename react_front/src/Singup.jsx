import axios from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

function Signup() {
  const navigate = useNavigate();
  const [member, setMember] = useState({
    id : '',
    pw : '',
    email : ''
  });

  const handleChange = (e) => {
    setMember({
      ...member,
      [e.target.name] : e.target.value
    })
  }

  return (
    <div>
      <h1>회원가입 페이지</h1>
      아이디 : <input type="text" name="username" onChange={handleChange}/> <br/>
      비밀번호 : <input type="text" name="password" onChange={handleChange}/> <br/>
      이메일 : <input type="text" name="email" onChange={handleChange}/> <br/>
      <button onClick={() => {
        axios.post(`${import.meta.env.VITE_SERVER_URL}/signup`, member)
        .then((res) => {
          console.log(res.data);
          alert(res.data);
          navigate('/');
        })
        .catch((err) => {
          console.log(err);
        })
      }}>회원가입</button>
    </div>
  );
}

export default Signup;