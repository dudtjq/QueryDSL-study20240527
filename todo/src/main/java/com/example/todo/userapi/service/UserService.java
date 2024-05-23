package com.example.todo.userapi.service;

import com.example.todo.auth.TokenProvider;
import com.example.todo.auth.TokenUserInfo;
import com.example.todo.exception.NoRegisteredArgumentException;
import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserSignUpRequestDTO;
import com.example.todo.userapi.dto.response.KakaoUserDTO;
import com.example.todo.userapi.dto.response.LoginResponseDTO;
import com.example.todo.userapi.dto.response.UserSignUpResponseDTO;
import com.example.todo.userapi.entity.Role;
import com.example.todo.userapi.entity.User;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Value("${upload.path}")
    private String uploadRootPath;

    @Value("${kakao.client_id}")
    private String KAKAO_CLIENT_ID;

    @Value("${kakao.redirect_url}")
    private String KAKAO_REDIRECT_URL;

    @Value("${kakao.client_secret}")
    private String KAKAO_CLIENT_SECRET;

    public boolean isDuplicate(String email) {
        if (userRepository.existsByEmail(email)) {
            log.warn("이메일이 중복되었습니다. - {}", email);
            return true;
        } else return false;
    }

    public UserSignUpResponseDTO create(
            final UserSignUpRequestDTO dto, final String uploadedFilePath) {
        String email = dto.getEmail();

        if (isDuplicate(email)) {
            throw new RuntimeException("중복된 이메일 입니다.");
        }

        // 패스워드 인코딩
        String encoded = passwordEncoder.encode(dto.getPassword());
        dto.setPassword(encoded);

        // dto를 User Entity로 변환해서 저장.
        User saved = userRepository.save(dto.toEntity(uploadedFilePath));
        log.info("회원 가입 정상 수행됨! - saved user - {}", saved);

        return new UserSignUpResponseDTO(saved);

    }

    public LoginResponseDTO authenticate(final LoginRequestDTO dto) {

        // 이메일을 통해 회원 정보 조회
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디 입니다."));

        // 패스워드 검증
        String rawPassword = dto.getPassword(); // 입력한 비번
        String encodedPassword = user.getPassword(); // DB에 저장된 암호화된 비번

        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        log.info("{}님 로그인 성공!", user.getUserName());

        // 로그인 성공 후에 클라이언트에게 뭘 리턴해 줄 것인가?
        // -> JWT를 클라이언트에 발급해 주어야 한다! -> 로그인 유지를 위해!
        String token = tokenProvider.createToken(user);

        return new LoginResponseDTO(user, token);

    }

    public LoginResponseDTO promoteToPremium(TokenUserInfo userInfo) {

        User user = userRepository.findById(userInfo.getUserId())
                .orElseThrow(() -> new NoRegisteredArgumentException("회원 조회에 실패했습니다."));

        // 일반(COMMON) 회원이 아니라면 예외 발생
        if (userInfo.getRole() != Role.COMMON) {
            throw new IllegalArgumentException("일반 회원이 아니라면 등급을 상승시킬 수 없습니다.");
        }

        // 등급 변경
        user.changeRole(Role.PREMIUM);
        User saved = userRepository.save(user);

        // 토큰을 재발급! (새롭게 변경된 정보가 반영된)
        String token = tokenProvider.createToken(saved);

        return new LoginResponseDTO(saved, token);
    }

    /**
     * 업로드 된 파일을 서버에 저장하고 저장 경로를 리턴
     *
     * @param profileImage - 업로드 된 파일 정보
     * @return 실제로 저장된 이미지 경로
     * */
    public String uploadProFileImage(MultipartFile profileImage) throws IOException {

        // 루트 디렉토리가 실존하는 지 확인 후 존재하는지 않으면 생성
        File rootDir = new File(uploadRootPath);

        if(!rootDir.exists()){
            rootDir.mkdirs();
        }

        // 파일명을 유니크하게 변경(이름 충돌 가능성을 대비)
        // UUID 와 원본파일을 컬럼 -> 규칙은 없음
        String uniqueFileName
                = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();

        // 파일을 저장
        File uploadFile = new File(uploadRootPath + "/" + uniqueFileName);
        profileImage.transferTo(uploadFile);

        return uniqueFileName;

    }

    public String findProfilePath(String userId) {

        User user
                = userRepository.findById(userId).orElseThrow(() -> new RuntimeException());

        String profileImage = user.getProfileImage();

        if(profileImage.startsWith("http://")){
            return profileImage;
        }

        // DB 에는 파일명만 저장 -> service 가 가지고 있는 Root Path 와 연결 해서 리턴
        return uploadRootPath + "/" + user.getProfileImage();

    }

    public LoginResponseDTO kakaoService(String code) {

        // 인가 코드를 통해 토큰을 발급받기
        String accessToken = getKakaoAccessToken(code);
        log.info("token : {}", accessToken);

        // 토큰을 통해 사용자 정보를 가져오기
        KakaoUserDTO kakaoUserDTO = getKakaoUserInfo(accessToken);
        log.info("kakaoUserDTO 확인 값 : {}", kakaoUserDTO);
        
        // 일회성 로그인으로 처리 -> dto 를 바로 화면단에 처리
        // 회원가입 처리 -> 이메일 중복 검사 진행 -> 자체 jwt 를 생성해서 토큰을 화면단에 리턴
        // -> 화면단에선서는 적절한 url 을 선택하여 redirect 를 진행

        if(!isDuplicate(kakaoUserDTO.getKakaoAccount().getEmail())){
            // 이메일이 중복되지 않았음 -> 이젠에 카카오 로그인 한 적이 없음 -> DB에 데이터 세팅
            User saved = userRepository.save(kakaoUserDTO.toEntity(accessToken));
        }

        // 이메일이 중복이 됨! -> 이전에 로그인을 한적이 있다! -> DB에 데이터를 또 넣을 필요는 없다
        User foundUser = userRepository.findByEmail(
                kakaoUserDTO.getKakaoAccount().getEmail()).orElseThrow();

        //  우리 사이트에서 사용하는 jwt 를 생성.
        String token = tokenProvider.createToken(foundUser);

        // 기존에 로그인 했던 사용자의 access token 값을 update
        foundUser.changeAccessToken(accessToken);
        userRepository.save(foundUser);

        return new LoginResponseDTO(foundUser, token);


    }

    private KakaoUserDTO getKakaoUserInfo(String accessToken) {

        String requestURI = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<KakaoUserDTO> responseEntity
                = restTemplate.exchange(requestURI, HttpMethod.GET,
                new HttpEntity<>(headers), KakaoUserDTO.class);

        // 응답 바디 꺼내기
        KakaoUserDTO responseData = responseEntity.getBody();
        log.info("user profile : {}",responseData);

        return responseData;



    }

    // 토큰 뽑는 메서드
    private String getKakaoAccessToken(String code) {

        // 오청 uri
        String requestURI = "https://kauth.kakao.com/oauth/token";

        // 오청 헤더 생성
        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 요청 바디 (파라미터 설정)
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code"); // 카카오 공식 문서 기준 값으로 세팅
        params.add("client_id", KAKAO_CLIENT_ID); // 카카오 디벨로퍼 REST API 키
        params.add("redirect_uri", KAKAO_REDIRECT_URL); // 카카오 디벨로퍼 등록된 redirect uri
        params.add("code", code); // 프론트에서 인가 코드 요청시 전달받은 코드값
        params.add("client_secret", KAKAO_CLIENT_SECRET); // 카카오 디벨로퍼 client secret(활성화 시 추가해 줘야 함)

        // 헤더와 바디 정보를 합치기 위해 HttpEntity 객체 생성
        HttpEntity<Object> requestEntity = new HttpEntity<>(params, headers);

        // 카카오 서버로 POST 통신
        RestTemplate restTemplate = new RestTemplate();

        // 통신을 보내면서 응답데이터를 리턴
        // param1: 요청 url
        // param2: 요청 메서드 (전송 방식)
        // param3: 헤더와 요청 파라미터정보 엔터티
        // param4: 응답 데이터를 받을 객체의 타입 (ex: dto, map)
        // 만약 구조가 복잡한 경우에는 응답 데이터 타입을 String 으로 받아서
        // JSON-simple 라이브러리로 직접 해체.
        ResponseEntity<Map> responseEntity
                = restTemplate.exchange(requestURI, HttpMethod.POST, requestEntity, Map.class);

        /*
        * HTTP/1.1 200 OK
        Content-Type: application/json;charset=UTF-8
        {
        "token_type":"bearer",
        "access_token":"${ACCESS_TOKEN}",
        "expires_in":43199,
        "refresh_token":"${REFRESH_TOKEN}",
        "refresh_token_expires_in":5184000,
        "scope":"account_email profile"
        }
        */

        // 응답 데이터에서 필요한 정보를 가져 오기
        Map<String, Object> responseData = responseEntity.getBody();
        log.info("토큰 요청 응답 데이터 : {}", responseData);

        // 여러가지 데이터중 access_token 이라는 이름의 데이터를 리턴
        // Object 를 String 으로 형변환 해서 리턴
        return (String)responseData.get("access_token");



    }

    // 로그아웃 메서드
    public String logout(TokenUserInfo userInfo) {

        User foundUser = userRepository.findById(userInfo.getUserId()).orElseThrow();

        String accessToken = foundUser.getAccessToken();

        // accessToken 이 null 이 아니라면 카카오 로그인 한 유저이다
        if(accessToken != null){
            String reqURI = "https://kapi.kakao.com/v1/user/logout";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);

            ResponseEntity<String> responseData
                    = new RestTemplate()
                    .exchange(reqURI, HttpMethod.POST, new HttpEntity<>(headers), String.class);

            foundUser.changeAccessToken(null);
            userRepository.save(foundUser);

            return responseData.getBody();
        }
        
        return null;
    }
}