package com.fc.todolist.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.Job

// 기본 뷰모델을 받아서 액티비티에서 사용
internal abstract class BaseActivity<VM: BaseViewModel>: AppCompatActivity() {

    abstract val viewModel: VM

    private lateinit var fetchJob: Job

    // BaseActivity에 진입할 때 마다 데이터를 불러옴
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 데이터 불러오고
        fetchJob = viewModel.fetchData()
        // 추상함수로 데이터 호출 시점에 구독한 값을 가지고 화면을 구성
        observeData()
    }

    abstract fun observeData()

    override fun onDestroy() {
        if (fetchJob.isActive) {
            fetchJob.cancel()
        }
        super.onDestroy()
    }

}