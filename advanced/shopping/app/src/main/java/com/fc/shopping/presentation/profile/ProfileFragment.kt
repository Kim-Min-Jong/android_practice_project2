package com.fc.shopping.presentation.profile

import android.app.Activity
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.fc.shopping.R
import com.fc.shopping.databinding.FragmentProfileBinding
import com.fc.shopping.extensions.loadCenterCrop
import com.fc.shopping.presentation.BaseFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
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
            try {
                // 가져온 정보에서 세부정보 가져옴
                task.getResult(ApiException::class.java)?.let {
                    Log.d(TAG, "firebaseAuthWithGoogle: ${it.id}")
                    // 추후 토큰을 저장해야함
                    viewModel.saveToken(it.idToken ?: throw Exception())
                } ?: throw Exception()
            } catch (e: Exception) {
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
                handleLoginState(it)
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
                handleRegisteredState(state)
            }
            is ProfileState.Success.NotRegistered -> {
                // 등록이 안되어 있으면
                // 보여줄 필요가 없음
                profileGroup.isGone = true
                loginRequiredGroup.isVisible = true
            }
        }
    }

    private fun handleLoginState(state: ProfileState.Login) = with(binding) {
        // 유저 정보 가져오기
        val credential = GoogleAuthProvider.getCredential(state.idToken, null)
        // 정보를 가지고 로그인 시도
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                // 성공하면
                if (task.isSuccessful) {
                    // 유저 정보 생성
                    val user = firebaseAuth.currentUser
                    viewModel.setUserInfo(user)
                } else {
                    viewModel.setUserInfo(null)
                }
            }
    }

    private fun handleRegisteredState(state: ProfileState.Success.Registered) = with(binding) {
        profileGroup.isVisible = true
        loginRequiredGroup.isGone = true
        profileImageView.loadCenterCrop(state.profileImgUri.toString(), 60f)
        userNameTextView.text = state.userName

        if (state.productList.isEmpty()) {
            emptyResultTextView.isGone = false
            recyclerView.isGone = true
        } else {
            emptyResultTextView.isGone = true
            recyclerView.isGone = false
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