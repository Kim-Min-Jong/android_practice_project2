package com.fc.githubrepository

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.fc.githubrepository.data.database.DatabaseProvider
import com.fc.githubrepository.data.entity.GithubRepoEntity
import com.fc.githubrepository.databinding.ActivityRepositoryBinding
import com.fc.githubrepository.extensions.loadCenterInside
import com.fc.githubrepository.utility.RetrofitUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class RepositoryActivity : AppCompatActivity(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private var binding: ActivityRepositoryBinding? = null
    private val repositoryDao by lazy {
        DatabaseProvider.provideDB(applicationContext).repositoryDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepositoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val repositoryOwner = intent.getStringExtra(REPOSITORY_OWNER_KEY) ?: kotlin.run {
            toast("Repository Owner 이름이 없습니다.")
            finish()
            return
        }
        val repositoryName = intent.getStringExtra(REPOSITORY_NAME_KEY) ?: kotlin.run {
            toast("Repository 이름이 없습니다.")
            finish()
            return
        }

        launch {
            loadRepository(repositoryOwner, repositoryName)?.let {
                setData(it)
            } ?: run {
                finish()
            }
        }
    }

    private suspend fun loadRepository(
        repositoryOwner: String,
        repositoryName: String
    ): GithubRepoEntity? =
        withContext(coroutineContext) {
            var repositoryEntity: GithubRepoEntity? = null
            withContext(Dispatchers.IO) {
                val response =
                    RetrofitUtil.githubApiService.getRepository(repositoryOwner, repositoryName)
                if (response.isSuccessful) {
                    val body = response.body()
                    withContext(Dispatchers.Main) {
                        body?.let {
                            repositoryEntity = it
                        }
                    }
                }
            }
            repositoryEntity
        }

    private fun setData(githubRepoEntity: GithubRepoEntity) = with(binding) {
        this?.let {
            showLoading(false)
            ownerProfileImageView.loadCenterInside(githubRepoEntity.owner.avatarUrl, 42f)
            ownerNameAndRepoNameTextView.text =
                "${githubRepoEntity.owner.login}/${githubRepoEntity.name}"
            stargazersCountText.text = githubRepoEntity.stargazersCount.toString()
            githubRepoEntity.language?.let { language ->
                languageText.isGone = false
                languageText.text = language
            } ?: kotlin.run {
                languageText.isGone = true
                languageText.text = ""
            }
            descriptionTextView.text = githubRepoEntity.description
            updateTimeTextView.text = githubRepoEntity.updatedAt

            setLikeState(githubRepoEntity)
        }
    }

    private fun setLikeState(githubRepoEntity: GithubRepoEntity) = launch {
        withContext(Dispatchers.IO) {
            val repository = repositoryDao.getRepository(githubRepoEntity.fullName)
            val isLike = repository != null
            withContext(Dispatchers.Main) {
                setLikeImage(isLike)
                binding?.likeButton?.setOnClickListener {
                    likeGithubRepo(githubRepoEntity, isLike)
                }
            }
        }
    }

    private fun setLikeImage(isLike: Boolean) {
        binding?.likeButton?.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                if (isLike) {
                    R.drawable.ic_like
                } else {
                    R.drawable.ic_dislike
                }
            )
        )
    }

    private fun likeGithubRepo(repoEntity: GithubRepoEntity, isLike: Boolean) = launch {
        withContext(Dispatchers.IO) {
            if(isLike) {
                repositoryDao.remove(repoEntity.fullName)
            } else {
                repositoryDao.insert(repoEntity)
            }
            withContext(Dispatchers.Main) {
                setLikeImage(isLike.not())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun showLoading(isShown: Boolean) = with(binding) {
        this?.let {
            progressBar.isGone = isShown.not()
        }
    }

    private fun Context.toast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    companion object {
        const val REPOSITORY_OWNER_KEY = "REPOSITORY_OWNER_KEY"
        const val REPOSITORY_NAME_KEY = "REPOSITORY_NAME_KEY"
    }
}