package com.fc.todolist.presentation.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fc.todolist.data.entity.ToDoEntity
import com.fc.todolist.domain.todo.GetToDoListUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 필요한 usecase들
 * 1. GetToDoItem ysecase - ToDoList에 아이템이 들어온 케이스
 * 2. UpdateToDo usecase -
 * 3. DeleteAllToDoItem usecase
 */
internal class ListViewModel(
    private val getToDoListUseCase: GetToDoListUseCase
): ViewModel() {

    private var _toDoListLiveData = MutableLiveData<List<ToDoEntity>?>()
    val toDoListLiveData: MutableLiveData<List<ToDoEntity>?> = _toDoListLiveData

    fun fetchData(): Job = viewModelScope.launch {
        _toDoListLiveData.postValue(getToDoListUseCase())
    }


}