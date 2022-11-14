package com.fc.todolist.viewmodel.todo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fc.todolist.data.entity.ToDoEntity
import com.fc.todolist.domain.todo.InsertToDoListUseCase
import com.fc.todolist.presentation.list.ListViewModel
import com.fc.todolist.viewmodel.ViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.inject


/**
 *  [ListViewModel]을 테스트하기 위한 Unit Test Class
 *
 *  scenarios
 *  1. initData() - 데이터 초기화(mocking data 주입)
 *  2. test viewModel fetch - viewmodel에서 fetch란 함수를 불렀을 때 데이터를 잘 불러오는 지 확인
 *  3. test item update - fetch한 데이터가 업데이트가 되는 지 확인
 *  4. test item Delelte all - 리스트의 아이템을 삭제했을 시 모두 삭제가 잘되는지 확인
 *
 */
@ExperimentalCoroutinesApi
internal class ListViewModelTest: ViewModelTest() {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // viewmodel 주입받기
    private val viewModel: ListViewModel by inject()

    // UseCase 주입받기
    private val insertToDoListUseCase: InsertToDoListUseCase by inject()

    private val mockList = (0 until 10).map{
        ToDoEntity(
            id = it.toLong(),
            title = "title $it",
            description = "description $it",
            hasCompleted = false
        )
    }

    /**
     * 필요한 usecase들
     * 1. InsertToDoList usecase - ToDo List에 입력할 케이스
     * 2. GetToDoItem ysecase - ToDoList에 아이템이 들어온 케이스
     */

    @Before
    fun init() {
        initData()
    }

    // mockdata
    private fun initData() = runTest {
        insertToDoListUseCase(mockList)
    }



    // Test: viewModel Fetch - 입력된 데이터를 불러와서 검증한다.
    @Test
    fun `test viewModel fetch`(): Unit = runTest {
        val testObservable = viewModel.toDoListLiveData.test()
        viewModel.fetchData()
        testObservable.assertValueSequence(
            listOf(
                mockList
            )
        )
    }

}