package com.fc.todolist.di

import android.content.Context
import androidx.room.Room
import com.fc.todolist.data.local.db.ToDoDatabase
import com.fc.todolist.data.repository.DefaultToDoRepository
import com.fc.todolist.data.repository.ToDoRepository
import com.fc.todolist.domain.todo.*
import com.fc.todolist.domain.todo.DeleteAllToDoItemUseCase
import com.fc.todolist.domain.todo.DeleteToDoItemUseCase
import com.fc.todolist.domain.todo.GetToDoItemUseCase
import com.fc.todolist.domain.todo.GetToDoListUseCase
import com.fc.todolist.domain.todo.InsertToDoItemUseCase
import com.fc.todolist.domain.todo.InsertToDoListUseCase
import com.fc.todolist.domain.todo.UpdateToDoListUseCase
import com.fc.todolist.presentation.detail.DetailMode
import com.fc.todolist.presentation.detail.DetailViewModel
import com.fc.todolist.presentation.list.ListViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val appModule = module {

    single { Dispatchers.Main }
    single { Dispatchers.IO }

    factory { GetToDoListUseCase(get()) }
    factory { GetToDoItemUseCase(get()) }
    factory { InsertToDoListUseCase(get()) }
    factory { InsertToDoItemUseCase(get()) }
    factory { DeleteToDoItemUseCase(get()) }
    factory { DeleteAllToDoItemUseCase(get()) }
    factory { UpdateToDoListUseCase(get()) }

    single<ToDoRepository> { DefaultToDoRepository(get(), get()) }

    single { provideDB(androidApplication()) }
    single { provideToDoDao(get()) }

    viewModel { ListViewModel(get(), get(), get()) }
    viewModel { (detailMode: DetailMode, id: Long) -> DetailViewModel(detailMode, id, get(), get(), get(), get()) }

}

internal fun provideDB(context: Context): ToDoDatabase =
    Room.databaseBuilder(context, ToDoDatabase::class.java, ToDoDatabase.DB_NAME).build()

internal fun provideToDoDao(database: ToDoDatabase) = database.toDoDao()