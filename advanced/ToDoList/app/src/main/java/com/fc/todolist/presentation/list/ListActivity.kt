package com.fc.todolist.presentation.list

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import com.fc.todolist.R
import com.fc.todolist.databinding.ActivityListBinding
import com.fc.todolist.presentation.BaseActivity
import com.fc.todolist.presentation.detail.DetailActivity
import com.fc.todolist.presentation.detail.DetailActivity.Companion.DETAIL_MODE_KEY
import com.fc.todolist.presentation.detail.DetailMode
import com.fc.todolist.presentation.view.ToDoAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

internal class ListActivity : BaseActivity<ListViewModel>(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()
    override val viewModel: ListViewModel by inject()
    private var binding: ActivityListBinding? = null
    private val adapter = ToDoAdapter()

//    private val resultIntent = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()) { result ->
//            if(result.resultCode == RESULT_OK) {
//                val data = result.data?.getStringExtra(DETAIL_MODE_KEY)
//            }
//
//    }
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    // 옵저버 패턴 - 구독 (바라봄)
    override fun observeData() {
        viewModel.toDoListLiveData.observe(this) {
            when (it) {
                is ToDoListState.UnInitialized -> {
                    initViews(binding!!)
                }
                is ToDoListState.Loading -> {
                    handleLoadingState()
                }
                is ToDoListState.Success -> {
                    handleSuccessState(it)
                }
                is ToDoListState.Error -> {
                    handleErrorState()
                }
            }
        }
    }

    private fun initViews(binding: ActivityListBinding) = with(binding) {
        recyclerView.layoutManager =
            LinearLayoutManager(this@ListActivity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        refreshLayout.setOnRefreshListener {
            viewModel.fetchData()
        }

        addToDoButton.setOnClickListener {
            //상세화면 넘어가기
//            val intent = DetailActivity.getIntent(this@ListActivity, DetailMode.WRITE)
            startActivityForResult(
                DetailActivity.getIntent(this@ListActivity, DetailMode.WRITE),
                DetailActivity.FETCH_REQUEST_CODE
            )

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DetailActivity.FETCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            viewModel.fetchData()
        }
    }
    private fun handleLoadingState() = with(binding) {
        this?.let {
            refreshLayout.isRefreshing = true
        }
    }

    private fun handleSuccessState(state: ToDoListState.Success) = with(binding) {
        this?.let {
            refreshLayout.isEnabled = state.toDoList.isNotEmpty()
            refreshLayout.isRefreshing = false

            if (state.toDoList.isEmpty()) {
                emptyResultTextView.isGone = false
                recyclerView.isGone = true
            } else {
                emptyResultTextView.isGone = true
                recyclerView.isGone = false
                adapter.setToDoList(
                    state.toDoList,
                    toDoItemClickListener = {
                        // 상세화면 구현
                        startActivityForResult(
                            DetailActivity.getIntent(this@ListActivity, it.id, DetailMode.DETAIL),
                            DetailActivity.FETCH_REQUEST_CODE
                        )

                    }, toDoCheckListener = {
                        viewModel.updateEntity(it)
                    }
                )
            }
        }
    }

    private fun handleErrorState() {
        Toast.makeText(this, "에러가 발생했습니다", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_all -> {
                viewModel.deleteAll()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_item, menu)
        return true
    }
}