package com.fc.githubrepository

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.fc.githubrepository.data.dao.SearchHistoryDao
import com.fc.githubrepository.data.database.DatabaseProvider
import com.fc.githubrepository.data.entity.GithubOwner
import com.fc.githubrepository.data.entity.GithubRepoEntity
import com.fc.githubrepository.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private var binding: ActivityMainBinding? = null
    val repositoryDao by lazy { DatabaseProvider.provideDB(applicationContext).repositoryDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initViews()

        // mock data(임시데이터) 주입
        launch {
            addMockData()
            val githubRepositories = loadGithubRepositories()
            withContext(coroutineContext) {
                Log.e("asd", githubRepositories.toString())
            }
        }
    }

    private fun initViews() = with(binding) {
        this?.let{
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
        repositoryDao.insertAll(mockData)
    }

    private suspend fun loadGithubRepositories() = withContext(Dispatchers.IO) {
        val repositories = repositoryDao.getHistory()
        return@withContext repositories
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}