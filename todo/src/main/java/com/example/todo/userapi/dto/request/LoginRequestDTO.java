package com.example.todo.userapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter @Getter @ToString
@EqualsAndHashCode @AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequestDTO {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

}
