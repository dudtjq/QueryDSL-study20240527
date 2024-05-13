package com.study.jpa.chap01_basic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "tbl_product")
public class Product {

    // wrapper 클래스로 객체 타입을 잡아놓으면 null 체크가 가능함!
    @Id // primary_key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    @Column(name = "prod_id:") // 변수명을 변경 하고 싶을때 사용
    private Long id;

    @Column(name = "prod_name", nullable = false, length = 30)
    private String name;

    private int price;

    @Enumerated(EnumType.STRING)// 기본이 ORDINAL 임 문자열로 값을 알고 싶어 STRING 으로 선언
    private Category category;

    @Column(updatable = false) // 수정 불가
    @CreationTimestamp // insert 할때 디폴트 값으로 들어감
    private LocalDateTime createDate;

    @UpdateTimestamp
    private LocalDateTime updateTime;

    public enum Category{
        FOOD, FASHION, ELECTRONIC
    }



}
