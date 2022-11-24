package com.fc.shopping.presentation.profile

import android.app.Activity
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.fc.shopping.R
import com.fc.shopping.databinding.FragmentProfileBinding
import com.fc.shopping.presentation.BaseFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject

internal class ProfileFragment : BaseFragment<ProfileViewModel, FragmentProfileBinding>() {
    // 로그인 시 필요한 옵션들
    private val gso: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()
    }

    // google sign in
    private val gsc by lazy {
        GoogleSignIn.getClient(requireActivity(), gso)
    }

    // firebase auth 객체
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    // intent 전환 launcher
    private val loginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 인텐트에서 로그인 정보를 가져옴
            val task = GoogleSignIn.getSignedInAccountFromIntent(result?.data)
            try{
                // 가져온 정보에서 세부정보 가져옴
                task.getResult(ApiException::class.java)?.let{
                    Log.d(TAG, "firebaseAuthWithGoogle: ${it.id}")
                    // 추후 토큰을 저장해야함

                } ?: throw Exception()
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override val viewModel: ProfileViewModel by inject<ProfileViewModel>()
    override fun getViewBinding(): FragmentProfileBinding =
        FragmentProfileBinding.inflate(layoutInflater)

    override fun observeData() = viewModel.profileStateLiveData.observe(this) {
        when (it) {
            is ProfileState.UnInitialized -> {
                initViews(binding)
            }
            is ProfileState.Loading -> {
                handleLoadingState()
            }
            is ProfileState.Login -> {

            }
            is ProfileState.Success -> {
                handleSuccessState(it)
            }
            is ProfileState.Error -> {

            }
        }
    }

    private fun initViews(binding: FragmentProfileBinding) = with(binding) {
        loginButton.setOnClickListener {
            signInGoogle()
        }
        logoutButton.setOnClickListener {

        }
    }
    private fun handleLoadingState() = with(binding) {
        progressBar.isVisible = true
        loginRequiredGroup.isGone = true
    }
    private fun handleSuccessState(state: ProfileState.Success) = with(binding) {
        progressBar.isVisible = false
        when (state) {
            is ProfileState.Success.Registered -> {
                // 등록 되있을때의 동작
            }
            is ProfileState.Success.NotRegistered -> {
                // 등록이 안되어 있으면
                // 보여줄 필요가 없음
                profileGroup.isGone = true
                loginRequiredGroup.isVisible = true
            }
        }
    }


    //firebase 로그인
    private fun signInGoogle() {
        val signInTIntent = gsc.signInIntent
        loginLauncher.launch(signInTIntent)
    }

    companion object {
        const val TAG = "ProfileFragment"
    }

}