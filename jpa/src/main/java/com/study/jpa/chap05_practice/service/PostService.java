package com.study.jpa.chap05_practice.service;

import com.study.jpa.chap05_practice.dto.PageDTO;
import com.study.jpa.chap05_practice.dto.PageResponseDTO;
import com.study.jpa.chap05_practice.dto.PostDetailResponseDTO;
import com.study.jpa.chap05_practice.dto.PostListResponseDTO;
import com.study.jpa.chap05_practice.entity.Post;
import com.study.jpa.chap05_practice.repository.HashTagRepository;
import com.study.jpa.chap05_practice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional // JPA 레파지토리는 트랜젝션 단위로 동작하기 때문에 작성해야함
public class PostService {

    private final PostRepository postRepository;

    private final HashTagRepository hashTagRepository;

    public PostListResponseDTO getPosts(PageDTO dto) {

        //pageable 객체 생성
        Pageable pageable = PageRequest.of(
                dto.getPage() - 1,
                dto.getSize(),
                Sort.by("createDate").descending()
        );

        // 데이터 베이스에서 게시물 목록 조회
        Page<Post> posts = postRepository.findAll(pageable);

        // 게시물 정보만 꺼내기
        List<Post> postList = posts.getContent();

        // 게시물 정보를 응답용 DTO 의 형태에 맞게 변환
        List<PostDetailResponseDTO> detailList = postList.stream()
                .map(PostDetailResponseDTO::new)
                .toList();

        // DB에서 조회한 정보를 JSON 형태에 맞는 DTO 로 변환
        // 페이징 구성 정보와 위에 있는 게시물 정보를 또 다른 DTO 로 한번에 포장해서
        // 리턴할 예정 -> PostListResponseDTO
        return PostListResponseDTO.builder()
                // 총게시물의 수가 아닌 페이징에 의해 조회된 게시물의 게수
                .count(detailList.size())
                // JPA 가 준 페이지 정보가 담긴 객체를 DTO 에게 전달해서 그쪽에서 알고리즘 돌리게 시킴
                .pageInfo(new PageResponseDTO(posts))
                .posts(detailList)
                .build();


    }
}
