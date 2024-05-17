package com.example.todo.userapi.api;

import com.example.todo.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController{

    private final UserService userService;

    // 이메일 중복 확인 요청 처리
    // GET : /api/auth/check?email=zzzz@xxx.com
    // jpa 는 pk로 조회하는 메서드는 기본 제공되지만 , 다른 컬럼으로 조회하는 메서드는
    // 제공 되지 않는다.

    @GetMapping("/check")
    public ResponseEntity<?> emailCheck(String email){

        // 이메일이 비어 있는지 체크
        if(email.trim().isEmpty()){
            return ResponseEntity.badRequest().body("이메일이 없습니다.");
        }

        boolean b = userService.checkEmail(email);

        log.info("emailCheck : {} POST!!!", b);

        return ResponseEntity.ok().body(b);

    }





}
