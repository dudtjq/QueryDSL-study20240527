package com.study.jpa.chap05_practice.service;

import com.study.jpa.chap05_practice.dto.*;
import com.study.jpa.chap05_practice.entity.HashTag;
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

    public PostDetailResponseDTO getDetail(Long id) throws Exception {

        Post post = getPost(id);

        return new PostDetailResponseDTO(post);

    }

    private Post getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(id + "번 게시물이 존재하지 않습니다."));
        return post;
    }

    public PostDetailResponseDTO insert(PostCreateDTO dto) throws Exception {

        // 게시물 저장
        Post saved = postRepository.save(dto.toEntity());

        // 해시태그 저장
        List<String> hashTags = dto.getHashTags();
        // 두번 체크
        if(hashTags != null && !hashTags.isEmpty()){

            hashTags.forEach(ht -> {
                HashTag hashTag = HashTag.builder()
                        .tagName(ht)
                        .post(saved)
                        .build();
                HashTag saveHashTag = hashTagRepository.save(hashTag);

            /*
            Post Entity 는 DB에 save 를 진행할 때 HashTag 에 대한 내용을 갱신하지 않습니다.
            HashTag Entity 는 따로 save 를 진행합니다. (테이블이 각각 나뉘어 있음)
            HashTag 는 양방향 맵핑이 되어있는 연관관계의 주인이기 때문에 save 를
            진행할 때 Post 를 전달하므로 DB와 Entity 의 상태가 동일합니다.
            Post 는 HashTag 의 정보가 비어있는 상태입니다.
            Post Entity 에 연관관계 편의 메서드를 작성하여 save 된 HashTag 의
            내용을 동기화 해야 추후에 진행되는 과정에서 문제가 발생하지 않습니다.
            (Post 를 화면단으로 return -> HashTag 들도 같이 가야 함. -> 직접 갱신)
            (Post 를 다시 SELECT 해서 가져온다? -> INSERT 가 완료된 후에 SELECT 를
             때려야 됨 -> Entity Manager 로 강제 flush() INSERT 는
             트랜잭션 종료 후 실행.)
            */

                saved.addHashTag(saveHashTag);
            });

        }

        //저장된 insert 요청한 게시물 정보를 DTO 로 변환해서 리턴
        return new PostDetailResponseDTO(saved);

    }

    public PostDetailResponseDTO modify(PostModifyDTO dto) {
    
        // 수정 전 데이터를 조회
        Post postEntity = getPost(dto.getPostNo());

        // 수정 시작
        postEntity.setTitle(dto.getTitle());
        postEntity.setContent(dto.getContent());

        // 수정 완료
        Post modifyPost = postRepository.save(postEntity);

        return new PostDetailResponseDTO(modifyPost);

    }

    public void delete(Long id) {

        postRepository.deleteById(id);

    }
}
