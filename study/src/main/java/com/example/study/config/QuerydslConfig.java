package com.example.study.config;

import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// QueryDSL 문법을 사용하기 위한 필수 객체인 JPAQueryFactory 의 빈 등록을 위한 클래스
// 나중에 여러 개의 repository 에서 QueryDSL 을 사용하기 위한 빈 등록
@Configuration
@Slf4j
public class QuerydslConfig {

    @PersistenceContext // JPA 라이브러리를 사용한다면 객체 주입 가능
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory(){

        return new JPAQueryFactory(entityManager);

    }

}
