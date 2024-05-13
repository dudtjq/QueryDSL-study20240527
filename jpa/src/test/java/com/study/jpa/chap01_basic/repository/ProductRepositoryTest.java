package com.study.jpa.chap01_basic.repository;

import com.study.jpa.chap01_basic.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.study.jpa.chap01_basic.entity.Product.*;
import static com.study.jpa.chap01_basic.entity.Product.Category.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // jpa 는 트랜젝션 단위로 동작하기 때문에 설정
@Rollback(false) // 테스트 클래스에서 트렌젝션을 사용하면 Rollback 이 자동으로 됨 -> Rollback 막기
class ProductRepositoryTest {

        @Autowired
        ProductRepository productRepository;

        @Test
        @DisplayName("데이터 베이스에 상품을 저장 해야 한다")
        void testSave() {
            // given
            Product p = Product.builder()
                    .name("점장")
                    .price(100000)
                    .category(FASHION)
                    .build();

//            Product p = Product.builder()
//                    .name("아이폰")
//                    .price(1200000)
//                    .category(ELECTRONIC)
//                    .build();

//            Product p = Product.builder()
//                    .name("탕수육")
//                    .price(20000)
//                    .category(FOOD)
//                    .build();

//            Product p = Product.builder()
//                    .name("구두")
//                    .price(30000)
//                    .category(FASHION)
//                    .build();
            // when
            Product saved = productRepository.save(p);

            // then
            assertNotNull(saved);
        }

        @Test
        @DisplayName("id 가 2번인 테이터를 삭제 해야야한다")
        void testRemove() {

            // given
            Long id = 2L;

            // when
            productRepository.deleteById(id);

            // then

        }

        @Test
        @DisplayName("상품 전제 조회를 하면 상품의 갯수는 3개여야 한다")
        void testFindAll() {
            // given

            // when
            List<Product> products = productRepository.findAll();
            // then
            products.forEach(System.out::println);
            assertEquals(2, products.size());
        }

        @Test
        @DisplayName("3번 상품을 조회 하면 상품명이 구두일 것이다")
        void testFindOne() {
            // given
            Long id = 3L;
            // when
            // Optional<T> : NPE 을 방지 할 수 있도록 타입으로 강제
            // null 값을 객체로 감싸서 바로 확인하지 못하게 보호,
            // 후속 작업들을 메서드로 다양하게 제공
            Optional<Product> productRepositoryById = productRepository.findById(id);
            // then
            productRepositoryById.ifPresent(product -> assertEquals("구두", product.getName()));

            Product product = productRepositoryById.orElseThrow();
            assertNotNull(product);

            System.out.println("product = " + product);

        }

        @Test
        @DisplayName("1번 상품의 이름과 가격이 변경해야 한다")
        void testModify() {
            // given
            Long id = 1L;
            String newName = "짜장면";
            int newPrice = 6000;
            Product.Category newCategory = FOOD;
            // when
            /*
            * jpa 에서는 update 는 따로 메서드로 제공되지 않고
            * 조회한 후에 setter 로 변경하면 자동으로 update 문이 나감
            * 변경 후 save 를 호출 하면 update 가 나감
            * */
//            Optional<Product> products = productRepository.findAllById(id);
//            products.ifPresent(product -> {
//                product.setName(newName);
//                product.setPrice(newPrice);
//                product.setCategory(newCategory);
//
//                productRepository.save(product);
//            });

            // then
        }






}