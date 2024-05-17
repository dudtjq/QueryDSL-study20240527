import React, { useEffect, useState } from 'react';
import TodoHeader from './TodoHeader';
import TodoMain from './TodoMain';
import TodoInput from './TodoInput';
import '../scss/TodoTemplate.scss';
import { json } from 'react-router-dom';

const TodoTemplate = () => {
  // 백엔드 서버에 할 일 목록(json)을 요청 (fetch)해서 받아와여 함
  const API_BASE_URL = 'http://localhost:8181/api/todos';
  // todos 배열을 상태 관리
  const [todos, setTodos] = useState([]);

  /*
  TodoInput 에게 todoText를 받아오는 함수 
  자식 컴포넌트가 부모 컴포넌트에게 데이터를 전달할 때는
  일반적인 props 사용이 불가능
  부모 컴포넌트에서 함수를 선언(매개변수 꼭 선언) -> props로 함수를 전달
  자식 컴포넌트에서 전달 받은 함수를 호출 하면서 매개값으로 데이터를 전달
  */
  const addTodo = (todoText) => {
    const newTodo = {
      title: todoText,
    };

    fetch(API_BASE_URL, {
      method: 'POST',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify(newTodo),
    })
      .then((res) => {
        if (res.status === 200) {
          return res.json();
        } else {
          // status 코드에 따라 에러 처리를 다르게 진행 하면 됨.
          console.log('error occured!');
        }
      })
      .then((data) => {
        setTodos(data.todos);
      });
  };

  // 할 일 삭제 처리 함수
  const removeTodo = (id) => {
    fetch(`${API_BASE_URL}/${id}`, {
      method: 'DELETE',
    })
      .then((res) => res.json())
      .then((data) => setTodos(data.todos))
      .catch((err) => {
        console.log('err :', err);
        alert('잘못된 삭제 요청 입니다.');
      });
  };

  // 할일 체크 처리 함수
  const checkTodo = (id, done) => {
    fetch(API_BASE_URL, {
      method: 'PATCH',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify({
        id,
        done: !done,
      }),
    })
      .then((res) => res.json())
      .then((json) => setTodos(json.todos));
  };

  // 체크가 안 된 할 일의 개수를 카운트 하기
  const countRestTodo = () => {
    const noChcekTodo = todos.filter((todo) => !todo.done);
    return noChcekTodo.length;
  };

  useEffect(() => {
    // 페이지가 처음 렌더링 됨과 동시에 할 일 목록을 서버에 요청해서 뿌리기
    fetch(API_BASE_URL)
      .then((res) => res.json())
      .then((json) => {
        console.log(json);

        // fetch를 통해 받아온 데이터를 상태 변수애 할당
        setTodos(json.todos);
      });
  }, []);

  return (
    <div className="TodoTemplate">
      <TodoHeader count={countRestTodo} />
      <TodoMain todoList={todos} remove={removeTodo} check={checkTodo} />
      <TodoInput addTodo={addTodo} />
    </div>
  );
};

export default TodoTemplate;
