package com.fc.todolist.presentation.list

import com.fc.todolist.data.entity.ToDoEntity

sealed class ToDoListState{
    object UnInitialized: ToDoListState()

    object Loading: ToDoListState()

    data class Success(
        val toDoList: List<ToDoEntity>
    ): ToDoListState()

    object Error: ToDoListState()
}
