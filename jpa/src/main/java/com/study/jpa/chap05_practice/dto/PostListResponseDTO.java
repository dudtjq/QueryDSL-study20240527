package com.study.jpa.chap05_practice.dto;

import lombok.*;

import java.util.List;

@Setter @ToString @Getter
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode
@Builder
public class PostListResponseDTO {

    private int count;

    private List<PostDetailResponseDTO> posts;

    private PageResponseDTO pageInfo;


}
