package com.study.jpa.chap02_querymethod.entity;

import com.study.jpa.chap02_querymethod.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class StudentRepositoryTest {

    @Autowired
    StudentRepository studentRepository;

    //    @BeforeAll: 해당 테스트 클래스를 초기화할 때 딱 한번 수행되는 메서드. (static으로 선언해야 함.)
    //@BeforeEach // 각각의 테스트 메서드 실행 직전에 수행되는 메서드.
    void insertData() {
        Student s1 = Student.builder()
                .name("춘식이")
                .city("서울시")
                .major("수학과")
                .build();
        Student s2 = Student.builder()
                .name("언년이")
                .city("부산시")
                .major("수학교육과")
                .build();
        Student s3 = Student.builder()
                .name("대길이")
                .city("한양도성")
                .major("체육과")
                .build();

        studentRepository.save(s1);
        studentRepository.save(s2);
        studentRepository.save(s3);
    }

    @Test
    @DisplayName("이름이 춘식이인 학생의 정보를 조회해야 한다.")
    void testFindByName() {
        // given
        String name = "춘식이";
        // when
        List<Student> students = studentRepository.findByName(name);

        // then
        assertEquals(1, students.size());

        System.out.println("students.get(0) = " + students.get(0));
    }

    @Test
    @DisplayName("testFindByCityAndMajor")
    void testFindByCityAndMajor() {
        // given
        String city = "부산시";
        String major = "수학교육과";

        // when
        List<Student> students = studentRepository.findByCityAndMajor(city, major);

        // then
        assertEquals(2, students.size());
        assertEquals("언년이", students.get(0).getName());

        System.out.println("students.get(0) = " + students.get(0));
    }

    @Test
    @DisplayName("testFindByMajorContaining")
    void testFindByMajorContaining() {
        // given
        String major = "수학";
        // when
        List<Student> students = studentRepository.findByMajorContaining(major);

        // then
        assertEquals(4, students.size());

        System.out.println("\n\n\n");
        students.forEach(System.out::println);
        System.out.println("\n\n\n");

    }

    @Test
    @DisplayName("testNativeSQL")
    void testNativeSQL() {
        // given
        String name = "대길이";
        // when
        List<Student> students = studentRepository.findNameWithSQL(name);

        // then
        assertEquals("한양도성", students.get(0).getCity());

        System.out.println("\n\n\n");
        students.forEach(stu -> System.out.println(stu));

    }

    @Test
    @DisplayName("testFindCityWithJPQL")
    void testFindCityWithJPQL() {
        // given
        String city = "서울시";
        // when
        List<Student> list = studentRepository.getByCityWithJPQL(city);

        // then
        assertEquals("춘식이", list.get(0).getName());
        System.out.println("\n\n\n");
        list.forEach(System.out::println);
        System.out.println("\n\n\n");

    }

    @Test
    @DisplayName("testSearchNameJPQL")
    void testSearchNameJPQL() {
        // given
        String name = "이";
        // when
        List<Student> students = studentRepository.searchByNameWithJPQL(name);

        // then
        assertEquals(6, students.size());
        System.out.println("\n\n\n");
        students.forEach(System.out::println);
        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("JPQL로 삭제하기")
    void testDeleteByJPQL() {
        // given
        String name = "대길이";
        // when
        studentRepository.deleteByNameWithJPQL(name);

        // then
        List<Student> students = studentRepository.findByName(name);

        assertEquals(0, students.size());
    }

}