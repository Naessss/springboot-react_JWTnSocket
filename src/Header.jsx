import { Link } from 'react-router-dom';

function Header({isAuth, setIsAuth}) {
  const logout = () => {
    sessionStorage.removeItem('jwt');
    setIsAuth(false);
  }

  return (
    <div>
      {
        isAuth ? 
        <Link to="/"  onClick={logout}>로그아웃</Link>
        : <Link to="/login">로그인</Link>
      }
      &nbsp;
      <Link to="/signup">회원가입</Link>
      <Link to="/write">글 작성</Link>
      <hr/>
    </div>
  );
}

export default Header;