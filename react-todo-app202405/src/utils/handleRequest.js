const handleError = (error, onLogout, redirection) => {
  if (error.response && error.response.status === 401) {
    console.log('onLogout : ', onLogout);
    console.log('redirection : ', redirection);
    alert('로그인 시간이 만료 되었습니다. 다시 로그인 해주세요.');
    onLogout();
    redirection('/login');
  } else if (error.response && error.response.status === 400) {
    // 400 에러 내용
  } else if (error.response && error.response.status === 403) {
    // 403 에러 내용
    alert('잘못된 정보입니다. 로그인 화면으로 넘어 갑니다.');
  }
};

const handleRequest = async (requestFunc, onSuccess, onLogout, redirection) => {
  try {
    const res = await requestFunc();

    if (res.status === 200) {
      onSuccess(res.data);
    }
  } catch (error) {
    console.log('error : ', error);
    handleError(error, onLogout, redirection);
  }
};

export default handleRequest;
