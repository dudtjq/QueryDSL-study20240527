package com.example.study.repository;

import com.example.study.entity.Member;
import com.example.study.entity.QMember;
import com.querydsl.core.types.dsl.BooleanExpression;
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

    @Override
    public List<Member> findUser(String nameParam, Integer ageParam) {
        return queryFactory.selectFrom(member)
                .where(nameEq(nameParam), ageEq(ageParam))
                .fetch();
    }

    
    // WHERE 절에 BooleanExpression 을 리턴하는 메서드를 직접 작성해야함
    // nameEq 는 전달받은 값이 없다면 null 을 리턴하고, 그렇지 않은 경우 논리 표현식 결과를 리턴함
    // WHERE 절에서는 null 값인 경우 조건을 건너 뜀(쿼리를 완성하지 않음)

    private BooleanExpression ageEq(Integer ageParam) {

        // return ageParam != null ? member.age.eq(ageParam) : null;

        if(ageParam != null){
            return member.age.eq(ageParam);
        }

        return null;
    }

    private BooleanExpression nameEq(String nameParma){
        if(nameParma != null && nameParma.isEmpty()){
            return member.userName.eq(nameParma);
        }
        return null;
    }

}
