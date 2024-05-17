package com.example.todo.todoapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CustomServletConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
                // CORS 설정을 적용할 url
        registry.addMapping("/**")
                // 자원 공유를 허락할 origin 울 설정 (origin : 프로토콜, ip 주소, 포트번호)
                .allowedOrigins("*")
                // 요청 방식
                .allowedMethods("HEAD", "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                // 원하는 시간 만큼 기존에 허락 했던 요청 정보를 기억할 시간
                .maxAge(300)
                // 요청을 허락할 헤더 정보 종류
                .allowedHeaders("Authorization", "Cache-Control", "Content-Type");

    }
}
