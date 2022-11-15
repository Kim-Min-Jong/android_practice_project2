package com.fc.todolist.viewmodel.todo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fc.todolist.data.entity.ToDoEntity
import com.fc.todolist.domain.todo.InsertToDoItemUseCase
import com.fc.todolist.presentation.detail.DetailMode
import com.fc.todolist.presentation.detail.DetailViewModel
import com.fc.todolist.presentation.detail.ToDoDetailState
import com.fc.todolist.presentation.list.ListViewModel
import com.fc.todolist.presentation.list.ToDoListState
import com.fc.todolist.viewmodel.ViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.parameter.parametersOf
import org.koin.test.inject

/**
 *  [DetailViewModel]을 테스트하기 위한 Unit Test Class
 *
 *  scenarios
 *  1. initData() - 데이터 초기화(mocking data 주입)
 *  2. test viewModel fetch - viewmodel에서 fetch란 함수를 불렀을 때 데이터를 잘 불러오는 지 확인
 *  3. test item update - fetch한 데이터가 업데이트가 되는 지 확인
 *  4. test item Delelte  - 리스트의 아이템을 삭제했을 시 삭제가 잘되는지 확인
 *
 */

@ExperimentalCoroutinesApi
internal class DetailViewModelTest : ViewModelTest() {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val id = 1L

    private val detailViewModel by inject<DetailViewModel> { parametersOf(DetailMode.DETAIL, id) }
    private val listViewModel by inject<ListViewModel>()

    private val insertToDoItemUseCase: InsertToDoItemUseCase by inject()

    private val todo = ToDoEntity(
        id = id,
        title = "title $id",
        description = "description $id",
        hasCompleted = false
    )

    @Before
    fun init() {
        initData()
    }

    // mockdata
    private fun initData() = runTest {
        insertToDoItemUseCase(todo)
    }

    @Test
    fun `test viewModel fetch`() = runTest {
        val testObservable = detailViewModel.toDoDetailLiveData.test()

        detailViewModel.fetchData()

        testObservable.assertValueSequence(
            listOf(
                ToDoDetailState.UnInitialized,
                ToDoDetailState.Loading,
                ToDoDetailState.Success(todo)
            )
        )
    }

    @Test
    fun `test item Delete todo`() = runTest {
        val detailTestObservable = detailViewModel.toDoDetailLiveData.test()
        detailViewModel.deleteToDo()
        detailTestObservable.assertValueSequence(
            listOf(
                ToDoDetailState.UnInitialized,
                ToDoDetailState.Loading,
                ToDoDetailState.Delete
            )
        )
        val listTestObservable = listViewModel.toDoListLiveData.test()
        listViewModel.fetchData()
        listTestObservable.assertValueSequence(
            listOf(
                ToDoListState.UnInitialized,
                ToDoListState.Loading,
                ToDoListState.Success(listOf())
            )
        )
    }

    @Test
    fun `test update todo`() = runTest {
        val detailTestObservable = detailViewModel.toDoDetailLiveData.test()
        val updateTitle = "title 1 update"
        val updateDescription = "description 1 update"

        val updateToDo = todo.copy(
            title = updateTitle,
            description = updateDescription
        )

        detailViewModel.writeToDo(
            title = updateTitle,
            description = updateDescription
        )

        detailTestObservable.assertValueSequence(
            listOf(
                ToDoDetailState.UnInitialized,
                ToDoDetailState.Loading,
                ToDoDetailState.Success(updateToDo)
            )
        )
    }
}