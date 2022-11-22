package com.fc.shopping.presentation.profile

import com.fc.shopping.databinding.FragmentProfileBinding
import com.fc.shopping.presentation.BaseFragment
import org.koin.android.ext.android.inject

internal class ProfileFragment: BaseFragment<ProfileViewModel, FragmentProfileBinding>() {
    override val viewModel: ProfileViewModel by inject<ProfileViewModel>()

    override fun getViewBinding(): FragmentProfileBinding = FragmentProfileBinding.inflate(layoutInflater)
    override fun observeData() {
    }

    companion object {
        const val TAG = "ProfileFragment"
    }

}