package com.fc.todolist.viewmodel.todo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fc.todolist.data.entity.ToDoEntity
import com.fc.todolist.presentation.detail.DetailMode
import com.fc.todolist.presentation.detail.DetailViewModel
import com.fc.todolist.presentation.detail.ToDoDetailState
import com.fc.todolist.presentation.list.ListViewModel
import com.fc.todolist.presentation.list.ToDoListState
import com.fc.todolist.viewmodel.ViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.koin.core.parameter.parametersOf
import org.koin.test.inject

/**
 *  [DetailViewModel]을 테스트하기 위한 Unit Test Class
 *
 *  scenarios
 *  1. test viewModel fetch - viewmodel에서 fetch란 함수를 불렀을 때 데이터를 잘 불러오는 지 확인
 *  2. test item insert - fetch한 데이터가 주입이 되는 지 확인
 *
 */

@ExperimentalCoroutinesApi
internal class DetailViewModelForWriteTest : ViewModelTest() {
    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val id = 0L

    private val detailViewModel by inject<DetailViewModel> { parametersOf(DetailMode.DETAIL, id) }
    private val listViewModel by inject<ListViewModel>()

    private val todo = ToDoEntity(
        id = id,
        title = "title $id",
        description = "description $id",
        hasCompleted = false
    )

    @Test
    fun `test viewModel fetch`() = runTest {
        val testObservable = detailViewModel.toDoDetailLiveData.test()
        detailViewModel.fetchData()
        testObservable.assertValueSequence(
            listOf(
                ToDoDetailState.UnInitialized,
                ToDoDetailState.Write
            )
        )
    }

    @Test
    fun `test insert todo`() = runTest {
        val detailTestObservable = detailViewModel.toDoDetailLiveData.test()
        val listTestObservable = listViewModel.toDoListLiveData.test()
        detailViewModel.writeToDo(
            title = todo.title,
            description = todo.description
        )

        detailTestObservable.assertValueSequence(
            listOf(
                ToDoDetailState.UnInitialized,
                ToDoDetailState.Loading,
                ToDoDetailState.Success(todo)
            )
        )

        assert(detailViewModel.detailMode == DetailMode.DETAIL)
        assert(detailViewModel.id == id)

        listViewModel.fetchData()
        listTestObservable.assertValueSequence(
            listOf(
                ToDoListState.UnInitialized,
                ToDoListState.Loading,
                ToDoListState.Success(listOf(todo))
            )
        )
    }
}