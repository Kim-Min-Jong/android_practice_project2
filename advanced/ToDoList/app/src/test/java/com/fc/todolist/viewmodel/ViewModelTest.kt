package com.fc.todolist.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.fc.todolist.di.appTestModule
import com.fc.todolist.livedata.LiveDataTestObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@ExperimentalCoroutinesApi
internal abstract class ViewModelTest: KoinTest {

    // 규칙 설정
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    //Mock 데이터
    @Mock
    private lateinit var context: Application

    // dispatcher
   private val dispatcher = StandardTestDispatcher()

    // test 전 셋업
    @Before
    fun setup() {
        startKoin{
            androidContext(context)
            modules(appTestModule)
        }
        Dispatchers.setMain(dispatcher) // main dispatcher 설정
    }

    // 테스트 후 객체 정리
    @After
    fun tearDown() {
        stopKoin()
        Dispatchers.resetMain() // Main Dispatcher를 초기화 해주어야 메모리 릭이 발생하지 않음
    }

    // livedata test observer 설정
    protected fun<T> LiveData<T>.test(): LiveDataTestObserver<T> {
        val testObserver = LiveDataTestObserver<T>()
        observeForever(testObserver)
        return testObserver
    }
}