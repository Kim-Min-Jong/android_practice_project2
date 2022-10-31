package com.fc.githubrepository

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isGone
import com.fc.githubrepository.databinding.ActivityMainBinding
import com.fc.githubrepository.utility.AuthTokenProvider
import com.fc.githubrepository.utility.RetrofitUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private var binding: ActivityMainBinding? = null
    private val authTokenProvider by lazy { AuthTokenProvider(applicationContext) }
    val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initViews()
    }

    private fun initViews() = with(binding) {
        this?.let {
            loginButton.setOnClickListener {
                loginGithub()
            }
        }
    }

    private fun loginGithub() {
        val loginUri = Uri.Builder().scheme("https").authority("github.com")
            .appendPath("login")
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
            .build()

        CustomTabsIntent.Builder().build().also {
            it.launchUrl(this, loginUri)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        intent.data?.getQueryParameter("code")?.let { code ->
            // 액세스토큰 받아오기
            launch(coroutineContext) {
                showProgress()
                getAccessToken(code)
                dismissProgress()
            }
        }
    }

    private suspend fun getAccessToken(code: String) = withContext(Dispatchers.IO) {
        val response = RetrofitUtil.authApiService.getAccessToken(
            BuildConfig.GITHUB_CLIENT_ID,
            BuildConfig.GITHUB_CLIENT_SECRET,
            code
        )
        if (response.isSuccessful) {
            val accessToken = response.body()?.accessToken ?: ""
            Log.e("token", accessToken ?: "")

            if (accessToken.isNotEmpty()) {
                authTokenProvider.updateToken(accessToken)
            } else{
                Toast.makeText(this@MainActivity, "accessToken이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private suspend fun showProgress() = withContext(coroutineContext) {
        with(binding!!) {
            loginButton.isGone = true
            progressBar.isGone = false
            progressTextView.isGone = false
        }
    }

    private suspend fun dismissProgress() = withContext(coroutineContext) {
        with(binding!!) {
            loginButton.isGone = false
            progressBar.isGone = true
            progressTextView.isGone = true
        }
    }
}
