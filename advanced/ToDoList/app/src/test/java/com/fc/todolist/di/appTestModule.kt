package com.fc.todolist.di

import com.fc.todolist.data.repository.TestToDoRepository
import com.fc.todolist.data.repository.ToDoRepository
import com.fc.todolist.domain.todo.GetToDoListUseCase
import com.fc.todolist.domain.todo.InsertToDoListUseCase
import com.fc.todolist.presentation.list.ListViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val appTestModule = module {

    //viewModel 등록
    viewModel {
        ListViewModel(get())
    }

    //UseCase 등록
    factory {
        GetToDoListUseCase(get())
    }
    factory {
        InsertToDoListUseCase(get())
    }

    //Repository 등록
    single<ToDoRepository> {
        TestToDoRepository()
    }
}