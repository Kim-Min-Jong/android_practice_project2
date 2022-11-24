package com.fc.shopping.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fc.shopping.data.preference.PreferenceManager
import com.fc.shopping.presentation.BaseViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class ProfileViewModel(
    private val preferenceManager: PreferenceManager
) : BaseViewModel() {
    private var _profileStateLiveData = MutableLiveData<ProfileState>(ProfileState.UnInitialized)
    val profileStateLiveData: LiveData<ProfileState> = _profileStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        setState(ProfileState.Loading)
        // 토큰 값을 받아 로그인 된지 안된지 판별
        preferenceManager.getIdToken()?.let {
            setState(ProfileState.Login(it))
        } ?: kotlin.run {
            setState(ProfileState.Success.NotRegistered)
        }
    }

    private fun setState(state: ProfileState) {
        _profileStateLiveData.postValue(state)
    }

    fun saveToken(idToken: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            preferenceManager.putIdToken(idToken)
            fetchData()
        }
    }

    fun setUserInfo(user: FirebaseUser?) = viewModelScope.launch {
        // 유저 상태에 따라 유저 정보 저장
        user?.let {
            setState(
                ProfileState.Success.Registered(
                    it.displayName ?: "익명",
                    it.photoUrl,
                    listOf()
                )
            )
        } ?: kotlin.run {
            setState(ProfileState.Success.NotRegistered)
        }
    }
}
