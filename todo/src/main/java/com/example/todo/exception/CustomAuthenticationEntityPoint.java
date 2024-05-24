package com.example.todo.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component // Bean Configuration 파일에 Bean 을 따로 등록하지 않아도 사용할 수 있다.
public class CustomAuthenticationEntityPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

       log.warn("AuthenticationEntryPoint 에서 commence 가 호출 됨!");

       /*
       * AuthenticationEntryPoint 인터페이스를 구현한 클래스를 제작하고 , WebSecurityConfig 
       * 에 등록하면 인증과정 에서 발생하는 예외를 일괄적으로 처리 할 수 있음
       * 401 상태에 관련된 예외들만 반응함 (로그인이 안됐거나 토큰에 문제가 있는 경우)
       * 만약 인증과 꼭 관련된 것이 아닌 다른 예외도 세밀하게 처리해야 한다면 Exception 전용
       * 필터를 구축하는 것이 좋음
       * */



    }
}
