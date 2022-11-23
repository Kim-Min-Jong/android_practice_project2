package com.fc.shopping.presentation.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fc.shopping.presentation.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class MainViewModel: BaseViewModel() {
    private var _mainStateLiveData = MutableLiveData<MainState>()
    val mainStateLiveData = _mainStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {

    }

    fun refreshOrderList() = viewModelScope.launch {
        _mainStateLiveData.postValue(MainState.RefreshOrderList)
    }
}