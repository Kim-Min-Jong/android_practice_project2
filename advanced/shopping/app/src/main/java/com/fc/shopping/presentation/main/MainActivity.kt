package com.fc.shopping.presentation.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fc.shopping.R
import com.fc.shopping.databinding.ActivityMainBinding
import com.fc.shopping.presentation.BaseActivity
import com.fc.shopping.presentation.BaseFragment
import com.fc.shopping.presentation.list.ProductListFragment
import com.fc.shopping.presentation.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.android.ext.android.inject

internal class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override val viewModel: MainViewModel by inject<MainViewModel>()
    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    // refresh 시 데이터 observe해서 새로고침
    override fun observeData() = viewModel.mainStateLiveData.observe(this) {
        when(it) {
            is MainState.RefreshOrderList -> {
                binding.bottomNav.selectedItemId = R.id.menu_profile
                val fragment = supportFragmentManager.findFragmentByTag(ProfileFragment.TAG)

                // 위 프래그먼트를 BaseFragment로 타입캐스팅해서 fetchData()해서 새로고침
                (fragment as? BaseFragment<*, *>)?.viewModel?.fetchData()
            }
       }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
    }
    private fun initViews() = with(binding) {
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.menu_products -> {
                    showFragment(ProductListFragment(), ProductListFragment.TAG)
                    true
                }
                R.id.menu_profile -> {
                    showFragment(ProfileFragment(),ProfileFragment.TAG)
                    true
                }
                else -> false
            }
        }
        showFragment(ProductListFragment(), ProductListFragment.TAG)
    }
    private fun showFragment(fragment: Fragment, tag: String) {
        val findFragment = supportFragmentManager.findFragmentByTag(tag)
        // 기존 프래그먼트가 올라와있다면 내려버림
        supportFragmentManager.fragments.forEach {
            supportFragmentManager.beginTransaction().hide(it).commit()
        }
        // 태그에 맞는 프래그먼트 올림
        findFragment?.let {
            supportFragmentManager.beginTransaction().show(it).commit()
        } ?: kotlin.run {  // 없으면 프래그먼트 컨테이너에 태그에 맞는 프래그먼트 추가
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment, tag)
                .commitAllowingStateLoss()
        }
    }
}