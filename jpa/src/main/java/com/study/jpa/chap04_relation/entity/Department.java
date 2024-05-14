package com.study.jpa.chap04_relation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString(exclude = {"employees"})
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
@Entity
@Table(name = "tbl_dept")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id")
    private Long id;

    @Column(name = "dept_name", nullable = false)
    private String name;

    // 양방향 맵핑에서는 상대방 엔터티의 갱신에 관여 할수 없다
    // 단순히 읽기 전용 (조회)으로만 사용해야 한다
    // mappedBy 에는 상배당 엔터티의 조인 되는 필드명을 작성 해야 한다
    @OneToMany(mappedBy = "department")
    private List<Employee> employees = new ArrayList<>(); // 초기화 필요(NPE 방지)


}
