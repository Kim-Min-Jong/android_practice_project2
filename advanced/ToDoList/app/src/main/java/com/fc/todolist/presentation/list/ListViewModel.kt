package com.fc.todolist.presentation.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fc.todolist.data.entity.ToDoEntity
import com.fc.todolist.domain.todo.DeleteAllToDoItemUseCase
import com.fc.todolist.domain.todo.GetToDoListUseCase
import com.fc.todolist.domain.todo.UpdateToDoListUseCase
import com.fc.todolist.presentation.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 필요한 usecase들
 * 1. GetToDoItem ysecase - ToDoList에 아이템이 들어온 케이스
 * 2. UpdateToDo usecase -
 * 3. DeleteAllToDoItem usecase
 */
internal class ListViewModel(
    private val getToDoListUseCase: GetToDoListUseCase,
    private val updateToDoListUseCase: UpdateToDoListUseCase,
    private val deleteAllToDoItemUseCase: DeleteAllToDoItemUseCase
) : BaseViewModel() {

    private var _toDoListLiveData = MutableLiveData<ToDoListState>(ToDoListState.UnInitialized)
    val toDoListLiveData: MutableLiveData<ToDoListState> = _toDoListLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        _toDoListLiveData.postValue(ToDoListState.Loading)
        _toDoListLiveData.postValue(ToDoListState.Success(getToDoListUseCase()))
    }

    fun updateEntity(todo: ToDoEntity) = viewModelScope.launch {
        updateToDoListUseCase(todo)
    }

    fun deleteAll() = viewModelScope.launch {
        _toDoListLiveData.postValue(ToDoListState.Loading)
        deleteAllToDoItemUseCase()
        _toDoListLiveData.postValue(ToDoListState.Success(getToDoListUseCase()))
    }
}