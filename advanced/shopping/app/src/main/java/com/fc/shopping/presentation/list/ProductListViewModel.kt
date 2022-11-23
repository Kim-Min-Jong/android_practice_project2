package com.fc.shopping.presentation.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fc.shopping.domain.GetProductListUseCase
import com.fc.shopping.presentation.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class ProductListViewModel(
    private val getProductListUseCase: GetProductListUseCase
): BaseViewModel() {
    private var _productListStateLiveData = MutableLiveData<ProductListState>(ProductListState.UnInitialized)
    val productListStateLiveData: LiveData<ProductListState> = _productListStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        //State를 통해 상태관리를 하면서 데이터를 업데이트 시켜줌
        setState(ProductListState.Loading)
        setState(ProductListState.Success(getProductListUseCase()))
    }

    private fun setState(state: ProductListState) {
        _productListStateLiveData.postValue(state)
    }
}