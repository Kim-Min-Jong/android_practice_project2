package com.fc.todolist.domain.todo

import com.fc.todolist.data.entity.ToDoEntity
import com.fc.todolist.data.repository.ToDoRepository
import com.fc.todolist.domain.UseCase

internal class UpdateToDoListUseCase(
    private val toDoRepository: ToDoRepository
): UseCase {

    suspend operator fun invoke(toDoEntity: ToDoEntity): Boolean {
        return toDoRepository.updateToDoItem(toDoEntity)
    }
}