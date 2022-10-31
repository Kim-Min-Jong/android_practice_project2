package com.fc.githubrepository

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isGone
import com.fc.githubrepository.data.dao.SearchHistoryDao
import com.fc.githubrepository.data.database.DatabaseProvider
import com.fc.githubrepository.data.entity.GithubOwner
import com.fc.githubrepository.data.entity.GithubRepoEntity
import com.fc.githubrepository.databinding.ActivityMainBinding
import com.fc.githubrepository.view.adapter.RepositoryRecyclerAdapter
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private var binding: ActivityMainBinding? = null
    private val repositoryDao by lazy { DatabaseProvider.provideDB(applicationContext).repositoryDao() }
    private lateinit var repoRecyclerAdapter: RepositoryRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initAdapter()
        initViews()
//        launch{
//            repositoryDao.clearAll()
//        }
    }

    private fun initAdapter() {
        repoRecyclerAdapter = RepositoryRecyclerAdapter()
    }

    private fun initViews() = with(binding) {
        this?.let{
            recyclerView?.adapter = repoRecyclerAdapter
            searchButton.setOnClickListener {
                startActivity(
                    Intent(this@MainActivity, SearchActivity::class.java)
                )
            }
        }
    }

    private suspend fun addMockData() = withContext(Dispatchers.IO) {
        val mockData = (0..10).map {
            GithubRepoEntity(
                name = "repo $it",
                fullName = "name $it",
                owner = GithubOwner(
                    "login",
                    "avatarUrl"
                ),
                description = null,
                language = null,
                updatedAt = Date().toString(),
                stargazersCount = it
            )
        }
//        repositoryDao.insertAll(mockData)
    }

    private suspend fun loadGithubRepositories() = withContext(Dispatchers.IO) {
        val repositories = repositoryDao.getHistory()
        return@withContext repositories
    }

    private suspend fun loadLikedRepositoryList() {
        val repoList = DatabaseProvider.provideDB(this@MainActivity).repositoryDao().getHistory()
        withContext(Dispatchers.Main) {
            setData(repoList)
        }
    }

    private fun setData(repoList: List<GithubRepoEntity>) = with(binding) {
        this?.let{
        if(repoList.isEmpty()) {
          emptyResultTextView.isGone = false
          recyclerView.isGone = true
        } else {
            emptyResultTextView.isGone = true
            recyclerView.isGone = false
            repoRecyclerAdapter.setSearchResultList(repoList) {
                startActivity(
                    Intent(this@MainActivity, RepositoryActivity::class.java).apply{
                        putExtra(RepositoryActivity.REPOSITORY_OWNER_KEY, it.owner.login)
                        putExtra(RepositoryActivity.REPOSITORY_NAME_KEY, it.name)
                    }
                )
            }
        }}
    }

    override fun onResume() {
        super.onResume()
        launch(coroutineContext) {
            loadLikedRepositoryList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}