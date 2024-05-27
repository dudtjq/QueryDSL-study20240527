package com.example.study.repository;

import com.example.study.entity.Member;
import com.example.study.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.study.entity.QMember.member;

// QueryDSL 용 인터페이스의 구현체는 반드시 이름이 Impl 로 끝나야 자동으로 인식이 되어서
// 기본 인터페이스 타입(MemberRepository)의 객체로도 사용이 가능
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findByName(String name) {
        return queryFactory.selectFrom(member)
                .where(member.userName.eq(name))
                .fetch();
    }
}
