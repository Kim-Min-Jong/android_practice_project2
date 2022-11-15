package com.fc.todolist.domain.todo

import com.fc.todolist.data.entity.ToDoEntity
import com.fc.todolist.data.repository.ToDoRepository
import com.fc.todolist.domain.UseCase

internal class GetToDoItemUseCase(
    private val toDoRepository: ToDoRepository
): UseCase {

    // invoke 함수 - 이름 없이 간편하게 호출될 수 있는 함수
    suspend operator fun invoke(itemId: Long): ToDoEntity? {
        return toDoRepository.getToDoItem(itemId)
    }
}