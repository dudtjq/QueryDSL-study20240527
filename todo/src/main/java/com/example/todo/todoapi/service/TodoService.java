package com.example.todo.todoapi.service;

import com.example.todo.todoapi.dto.request.TodoCreateRequestDTO;
import com.example.todo.todoapi.dto.request.TodoModifyRequestDTO;
import com.example.todo.todoapi.dto.response.TodoDetailResponseDTO;
import com.example.todo.todoapi.dto.response.TodoListResponseDTO;
import com.example.todo.todoapi.entity.Todo;
import com.example.todo.todoapi.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoListResponseDTO create(TodoCreateRequestDTO requestDTO) throws Exception {

        Todo saved = todoRepository.save(requestDTO.toEntity());

        log.info("할 일 저장 완료! 제목 : {}", requestDTO.getTitle());

        return retrieve();

    }

    // 할 일 목록 가져오기
    public TodoListResponseDTO retrieve() throws Exception {

        List<Todo> entityList = todoRepository.findAll();

        List<TodoDetailResponseDTO> dtoList = entityList.stream()
                .map(TodoDetailResponseDTO::new)
                .toList();

        return TodoListResponseDTO.builder()
                .todos(dtoList)
                .build();

    }

    public TodoListResponseDTO deleteTodo(final String todoId) throws Exception {


        Todo todo = todoRepository.findById(todoId).orElseThrow(
                () -> {log.error("아이디가 존재 하지 않아 삭제가 불가능 합니다. - Id = {}, "
                        , todoId);
        throw new RuntimeException("아이디가 존재 하지 않아 삭제가 불가능 합니다.");}
        );

            todoRepository.deleteById(todoId);

        return retrieve();
    }

    public TodoListResponseDTO update(final TodoModifyRequestDTO requestDTO) throws Exception {

        Optional<Todo> targetEntity = todoRepository.findById(requestDTO.getId());

        targetEntity.ifPresent(todo -> {
            todo.setDone(requestDTO.isDone());

            todoRepository.save(todo);
        });

        return retrieve();

    }
}
