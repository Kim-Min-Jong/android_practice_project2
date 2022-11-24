package com.fc.shopping.presentation.profile

import android.app.Activity
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.fc.shopping.R
import com.fc.shopping.databinding.FragmentProfileBinding
import com.fc.shopping.extensions.loadCenterCrop
import com.fc.shopping.extensions.toast
import com.fc.shopping.presentation.BaseFragment
import com.fc.shopping.presentation.adapter.ProductListAdapter
import com.fc.shopping.presentation.detail.ProductDetailActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import org.koin.android.ext.android.inject

internal class ProfileFragment : BaseFragment<ProfileViewModel, FragmentProfileBinding>() {
    // 로그인 시 필요한 옵션들
    private val gso: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
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
        Log.e(TAG, result.resultCode.toString())
        Log.e(TAG, Activity.RESULT_OK.toString())
        if (result.resultCode == Activity.RESULT_OK) {
            // 인텐트에서 로그인 정보를 가져옴
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // 가져온 정보에서 세부정보 가져옴
                task.getResult(ApiException::class.java)?.let {
                    Log.d(TAG, "firebaseAuthWithGoogle: ${it.id}")
                    // 추후 토큰을 저장해야함
                    viewModel.saveToken(it.idToken ?: throw Exception())
                } ?: throw Exception()
            } catch (e: Exception) {
                e.printStackTrace()
                handleErrorState()
            }
        }
    }
    private val adapter = ProductListAdapter()
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
                handleErrorState()
            }
        }
    }

    private fun initViews(binding: FragmentProfileBinding) = with(binding) {
        recyclerView.adapter = adapter
        loginButton.setOnClickListener {
            signInGoogle()
        }
        logoutButton.setOnClickListener {
            firebaseAuth.signOut()
            viewModel.signOut()
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
                Log.e(TAG,"registered")
            }
            is ProfileState.Success.NotRegistered -> {
                Log.e(TAG,"not registered")
                // 등록이 안되어 있으면
                // 보여줄 필요가 없음
                profileGroup.isGone = true
                loginRequiredGroup.isVisible = true
                adapter.setProductList(listOf())
            }
        }
    }

    private fun handleLoginState(state: ProfileState.Login) {
        binding.progressBar.isVisible = true
        // 유저 정보 가져오기
        val credential = GoogleAuthProvider.getCredential(state.idToken, null)
        // 정보를 가지고 로그인 시도
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                // 성공하면
                if (task.isSuccessful) {
                    // 유저 정보 생성
                    val user = firebaseAuth.currentUser
                    Log.e(TAG, user.toString())
                    viewModel.setUserInfo(user)
                } else {
                    viewModel.setUserInfo(null)
                    requireContext().toast("로그아웃이 되어 재로그인 필요합니다.")
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
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
            // 내 주문에서 상세 주문 내역으로 이동
            adapter.setProductList(state.productList) {
                startActivity(
                    ProductDetailActivity.newIntent(requireContext(), it.id)
                )
            }
        }
    }
    private fun handleErrorState() {
        requireContext().toast("에러가 발생했습니다.")
    }
    //firebase 로그인
    private fun signInGoogle() {
        val signInIntent = gsc.signInIntent
        loginLauncher.launch(signInIntent)
    }

    companion object {
        const val TAG = "ProfileFragment"
    }

}