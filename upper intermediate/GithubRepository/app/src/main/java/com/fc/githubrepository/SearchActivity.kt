package com.fc.githubrepository

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isGone
import com.fc.githubrepository.data.entity.GithubRepoEntity
import com.fc.githubrepository.databinding.ActivitySearchBinding
import com.fc.githubrepository.utility.RetrofitUtil
import com.fc.githubrepository.view.adapter.RepositoryRecyclerAdapter
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SearchActivity : AppCompatActivity(), CoroutineScope {
    private var binding: ActivitySearchBinding? = null
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var adapter: RepositoryRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initAdapter()
        initViews()
        bindViews()
    }

    private fun initAdapter() = with(binding) {
        this?.let {
            adapter = RepositoryRecyclerAdapter()
        }
    }

    private fun initViews() = with(binding) {
        this?.let {
            emptyResultTextView.isGone = true
            recyclerView.adapter = adapter
        }
    }

    private fun bindViews() = with(binding) {
        this?.let {
            searchButton.setOnClickListener {
                searchKeyword(searchBarInputView.text.toString())
            }
        }
    }

    private fun searchKeyword(keywordString: String) = launch {
        withContext(Dispatchers.IO) {
            try {
                withContext(Dispatchers.IO) {
                    val response = RetrofitUtil.githubApiService.searchRepositories(
                        query = keywordString
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        withContext(Dispatchers.Main) {
                            Log.e("response", body.toString())
                            body?.let { searchResponse ->
                                setData(searchResponse.items)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@SearchActivity, "검색하는 과정에서 에러가 발생했습니다. : ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setData(items: List<GithubRepoEntity>) {
        adapter.setSearchResultList(items) {
            startActivity(
                Intent(this,RepositoryActivity::class.java).apply {
                     putExtra(RepositoryActivity.REPOSITORY_OWNER_KEY, it.owner.login)
                     putExtra(RepositoryActivity.REPOSITORY_NAME_KEY, it.name)
                }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}