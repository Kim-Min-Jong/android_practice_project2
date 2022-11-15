package com.fc.todolist.data.repository

import com.fc.todolist.data.entity.ToDoEntity

/**
 * 1. insertToDoList
 * 2. getToDoList
 * 3. updateToDoItem
 * 4. getToDoItem
 */
interface ToDoRepository {
    suspend fun getToDoList(): List<ToDoEntity>

    suspend fun insertToDoList(toDoList: List<ToDoEntity>)

    suspend fun insertToDoItem(toDoItem: ToDoEntity): Long

    suspend fun updateToDoItem(toDoEntity: ToDoEntity): Boolean

    suspend fun getToDoItem(itemId: Long): ToDoEntity?

    suspend fun deleteAll()

    suspend fun deleteToDoItem(id: Long): Boolean
}