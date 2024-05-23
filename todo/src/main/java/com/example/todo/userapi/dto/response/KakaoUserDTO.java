package com.example.todo.userapi.dto.response;

import com.example.todo.userapi.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode
@Builder
public class KakaoUserDTO {

    private long id;

    @JsonProperty("connected_at")
    private LocalDateTime connectedAt;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Setter @Getter @ToString
    public static class KakaoAccount {

        private String email;
        private Profile profile;

        @Getter @Setter @ToString
        public static class Profile {
            private String nickname;

//            @JsonProperty("thumbnail_image_url")
//            private String thumbnailIImageUrl;

            @JsonProperty("profile_image_url")
            private String profileImageUrl;
        }



    }

    public User toEntity(String accessToken){
        return User.builder()
                .email(this.getKakaoAccount().getEmail())
                .userName(kakaoAccount.profile.nickname)
                .password("password!")
                .profileImage(this.kakaoAccount.profile.profileImageUrl)
                .accessToken(accessToken)
                .build();
    }


}
