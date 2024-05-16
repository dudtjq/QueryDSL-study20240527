package com.study.jpa.chap05_practice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter @Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor @NoArgsConstructor
@Builder
public class PostModifyDTO {

    @NotBlank
    @Size(min = 1, max = 20)
    private String title;

    private String content;

    @NotNull // 공백이나 빈 문자열이 들어올 수 없는 타입은 NotNull 로 선언
    private Long postNo;



}
