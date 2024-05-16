package com.example.todo.todoapi.dto.request;

import com.example.todo.todoapi.entity.Todo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @ToString
@EqualsAndHashCode
@AllArgsConstructor @NoArgsConstructor
@Builder
public class TodoCreateRequestDTO {

    @NotBlank
    @Size(min = 2, max = 30)
    private String title;

    public Todo toEntity(){
        return Todo.builder()
                .title(title)
                .build();

    }




}
