package com.study.jpa.chap05_practice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Setter @Getter
@ToString(exclude = {"HashTag"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_no")
    private Long id; // 글번호

    @Column(nullable = false) // not null
    private String writer; // 작성자

    private String content; // 글 내용

    @CreationTimestamp
    @Column(updatable = false) // 수정 불가
    private LocalDateTime createDate; // 작성 시간

    @UpdateTimestamp
    private LocalDateTime updateDate; // 수정 시간

}
