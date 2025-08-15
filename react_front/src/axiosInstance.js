import axios from 'axios';

// Axios 인스턴스 생성
const axiosInstance = axios.create({
  baseURL: `${import.meta.env.VITE_SERVER_URL}`,
  headers: {
    'Content-Type': 'application/json; charset=utf-8',
  },
});

// JWT를 Axios 요청 헤더에 추가하는 함수
function addJwtToRequest(config) {
  // 세션 스토리지에서 JWT를 가져옴
  const jwt = sessionStorage.getItem('jwt');
  
  // JWT가 있을 경우, 요청 헤더에 추가
  if (jwt) {
    config.headers['Authorization'] = jwt;
  }
  
  return config;
}

// Axios 요청 전에 JWT를 추가하는 인터셉터 설정
axiosInstance.interceptors.request.use(
  (config) => addJwtToRequest(config),
  (error) => Promise.reject(error)
);

export default axiosInstance;