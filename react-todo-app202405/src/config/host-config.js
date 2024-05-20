// 브라우저에서 현재 클라이언트의 호스트 이름 얻어오기
const clientHostName = window.location.hostname;

let backEndHostName; // 백엔드 서버 호스트 이름

// 현재 개발중인 리엑트 프로젝트의 ip는 localhost 임 -> 백엔드도 localhost 로 작업 진행중
// 하지만, 나중에는 도메인을 구입을 할 예정 -> 백엔드의 주소도 바뀔 수가 있다.
// 리액트 내에서 백엔드를 지목하면서 fetch 요청을 많이 진행하고 있기 때문에, 주소 변경의
// 가능성을 염두에 두고 호스트 네임을 전역적으로 관리하려는 의도로 설정하는 파일임
if (clientHostName === 'localhost') {
  // 개발 중
  backEndHostName = 'http://localhost:8181';
} else if (clientHostName === 'spring.com') {
  backEndHostName = 'https://api.spring.com';
}

export const API_BASE_URL = backEndHostName;
export const TODO = '/api/todos';
export const USER = '/api/auth';
