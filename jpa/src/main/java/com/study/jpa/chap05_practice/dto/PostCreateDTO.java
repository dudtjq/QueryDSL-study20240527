package com.study.jpa.chap05_practice.dto;

import com.study.jpa.chap05_practice.entity.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter @Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PostCreateDTO {

    // @NotNULL -> null 을 허용하지 않음 "", " " 은 허용
    // NotEmpty -> null , "" 를 허용 하지 않음 , " " 은 허용
    @NotBlank // -> null, "" " " 모두 허용하지 않음
    @Size(min = 2, max = 5)
    private String writer;

    @NotBlank
    @Size(min = 1, max = 20)
    private String title;

    private String content;

    private List<String> hashTags;


    // dto 를 entity 로 변환
    public Post toEntity(){
        return Post.builder()
                .writer(writer)
                .title(title)
                .content(content)
                // 해시태그는 여기서 넣는게 아님
                .build();
    }





}
