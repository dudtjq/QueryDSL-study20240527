package com.example.todo.userapi.service;

import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserSignUpRequestDTO;
import com.example.todo.userapi.dto.response.UserSignUpResponseDTO;
import com.example.todo.userapi.entity.User;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public boolean checkEmail(String email) {

        if (userRepository.existsByEmail(email)) {
            log.warn("이메일이 중복 되었습니다 - {}", email);
            return true;
        }else {
            log.info("사용가능한 이메일 입니다. - {}", email);
            return false;
        }

    }

    public UserSignUpResponseDTO create(UserSignUpRequestDTO dto) throws Exception{

        String email = dto.getEmail();

        if(checkEmail(email)){
            throw new RuntimeException("중복된 이메일 입니다.");
        }

        // 패스워드 인코딩
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

        User save = userRepository.save(dto.toEntity());
        log.info("회원가입 정상 수행 - save user : {}", save);

        return new UserSignUpResponseDTO(save);
    }

    public String authenticate(final LoginRequestDTO dto) throws Exception {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디 입니다."));

        String rawPassword = dto.getPassword();
        String encodedPassword = user.getPassword(); // 암호화 된 비번

        if(passwordEncoder.matches(rawPassword, encodedPassword)){
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        log.info("{}님 로그인 성공!", user.getUserName());

        // 로그인 성공후에 클라이 언트 에게 뭘 리턴 할 것인가?
        // -> JWT 를 클라이언트에게 발급해 주어야 함 -> 로그인 유지를 위해


        return "SUCCESS";

    }
}
