package com.example.study.repository;

import com.example.study.entity.Member;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

public interface MemberRepositoryCustom {

    // JPA 아님 상속 받지 않았음
    List<Member> findByName(String name);

    List<Member> findUser(String nameParam, Integer ageParam);

}
