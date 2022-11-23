package com.fc.shopping.presentation.list

import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import com.fc.shopping.databinding.FragmentProductListBinding
import com.fc.shopping.extensions.toast
import com.fc.shopping.presentation.BaseFragment
import com.fc.shopping.presentation.adapter.ProductListAdapter
import com.fc.shopping.presentation.detail.ProductDetailActivity
import com.fc.shopping.presentation.main.MainActivity

import org.koin.android.ext.android.inject

internal class ProductListFragment: BaseFragment<ProductListViewModel, FragmentProductListBinding>() {
    override val viewModel: ProductListViewModel by inject<ProductListViewModel>()
    private val adapter = ProductListAdapter()
    override fun getViewBinding(): FragmentProductListBinding = FragmentProductListBinding.inflate(layoutInflater)
    private val startProductDetailForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // 성공 이후의 동작
            if(result.resultCode == ProductDetailActivity.PRODUCT_ORDERED_RESULT_CODE) {
                (requireActivity() as MainActivity).viewModel.refreshOrderList()
            }
        }
    // 상태 변화시 상태에 따라 동작 수행
    override fun observeData() = viewModel.productListStateLiveData.observe(this){
        when(it) {
            is ProductListState.UnInitialized -> {
                initViews(binding)
            }
            is ProductListState.Loading -> {
                handleLoadingState()
            }
            is ProductListState.Success -> {
                handleSuccessState(it)
            }
            is ProductListState.Error -> {
                handleErrorState()
            }
        }
    }

    private fun initViews(binding: FragmentProductListBinding) = with(binding) {
        recyclerView.adapter = adapter

        refreshLayout.setOnRefreshListener {
            viewModel.fetchData()
        }

    }

    private fun handleLoadingState() = with(binding) {
        refreshLayout.isRefreshing = true
    }

    private fun handleSuccessState(state: ProductListState.Success) = with(binding) {
        // 상태가 없다면 리프레시 불가  로딩 중 상태가 없어지면
        refreshLayout.isRefreshing = false

        // 물품 리스트가 없으면
        if (state.productList.isEmpty()) {
            emptyResultTextView.isGone = false
            recyclerView.isGone = true
        } else { // 있으면
            emptyResultTextView.isGone = true
            recyclerView.isGone = false
            adapter.setProductList(state.productList) {
                startProductDetailForResult.launch(
                    ProductDetailActivity.newIntent(requireContext(), it.id)
                )
                requireContext().toast("Product Entity: $it")
            }
        }
    }

    private fun handleErrorState() {
        Toast.makeText(requireContext(), "에러가 발생했습니다.", Toast.LENGTH_SHORT).show()
    }
    companion object {
        const val TAG = "ProductListFragment"
    }

}