package com.fc.todolist.data.repository

import com.fc.todolist.data.entity.ToDoEntity

/**
 * 1. insertToDoList
 * 2. getToDoList
 *
 */
interface ToDoRepository {
    suspend fun getToDoList(): List<ToDoEntity>

    suspend fun insertToDoList(toDoList: List<ToDoEntity>)
}