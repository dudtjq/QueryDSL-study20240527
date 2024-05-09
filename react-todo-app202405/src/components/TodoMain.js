import React from 'react';
import '../scss/TodoMain.scss';
import TodoItem from './TodoItem';

const TodoMain = ({ todoList, remove, check }) => {
  return (
    <ul className="todo-list">
      {todoList.map((todo) => {
        return (
          <TodoItem key={todo.id} item={todo} remove={remove} check={check} />
        );
      })}
    </ul>
  );
};

export default TodoMain;
