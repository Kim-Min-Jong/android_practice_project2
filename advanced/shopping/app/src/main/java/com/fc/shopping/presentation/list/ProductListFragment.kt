package com.fc.shopping.presentation.list

import com.fc.shopping.databinding.FragmentProductListBinding
import com.fc.shopping.presentation.BaseFragment

import org.koin.android.ext.android.inject

internal class ProductListFragment: BaseFragment<ProductListViewModel, FragmentProductListBinding>() {
    override val viewModel: ProductListViewModel by inject<ProductListViewModel>()

    override fun getViewBinding(): FragmentProductListBinding = FragmentProductListBinding.inflate(layoutInflater)
    override fun observeData() {
    }

    companion object {
        const val TAG = "ProductListFragment"
    }

}