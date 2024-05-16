package com.example.todo.todoapi.service;

import com.example.todo.todoapi.dto.request.TodoCreateRequestDTO;
import com.example.todo.todoapi.dto.response.TodoListResponseDTO;
import com.example.todo.todoapi.entity.Todo;
import com.example.todo.todoapi.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoListResponseDTO create(TodoCreateRequestDTO requestDTO) {

        Todo saved = todoRepository.save(requestDTO.toEntity());

        log.info("할 일 저장 완료! 제목 : {}", requestDTO.getTitle());

        return retrieve();

    }

    // 할 일 목록 가져오기
    public TodoListResponseDTO retrieve(){



    }
}
