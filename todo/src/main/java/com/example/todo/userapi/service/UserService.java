package com.example.todo.userapi.service;

import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public boolean checkEmail(String email) {

        if (userRepository.existsByEmail(email)) {
            log.warn("이메일이 중복 되었습니다 - {}", email);
            return true;
        }else {
            log.info("사용가능한 이메일 입니다. - {}", email);
            return false;
        }

    }
}
