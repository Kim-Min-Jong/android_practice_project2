package com.fc.shopping.presentation.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fc.shopping.data.entity.product.ProductEntity
import com.fc.shopping.domain.GetProductItemUseCase
import com.fc.shopping.domain.OrderProductItemUseCase
import com.fc.shopping.presentation.BaseViewModel
import com.fc.shopping.presentation.list.ProductListState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class ProductDetailViewModel(
    private val productId: Long,
    private val getProductItemUseCase: GetProductItemUseCase,
    private val orderProductItemUseCase: OrderProductItemUseCase,

    ): BaseViewModel() {
    // livedata 초기화
    private var _productDetailState = MutableLiveData<ProductDetailState>(ProductDetailState.UnInitialized)
    val productDetailStateLiveData = _productDetailState

    private lateinit var productEntity: ProductEntity

    override fun fetchData(): Job = viewModelScope.launch {
        setState(ProductDetailState.Loading)
        getProductItemUseCase(productId)?.let {
            productEntity = it
            setState(ProductDetailState.Success(it))
        } ?: kotlin.run {
            setState(ProductDetailState.Error)
        }
    }

    fun orderProduct() = viewModelScope.launch {
        //order usecase를 통해 order 동작 실행행
        if(::productEntity.isInitialized){
            val productId = orderProductItemUseCase(productEntity)
            if(productId == productEntity.id) {
                setState(ProductDetailState.Order)
            }
        } else {
            setState(ProductDetailState.Error)
        }
   }
    private fun setState(state: ProductDetailState) {
        _productDetailState.postValue(state)
    }
}