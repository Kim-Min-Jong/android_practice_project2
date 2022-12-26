package com.fc.trackingdelivery.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.fc.trackingdelivery.R
import com.fc.trackingdelivery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView()
    }

    private fun initView() {
        val navigationController =
            (supportFragmentManager.findFragmentById(R.id.mainNavigationHostContainer) as NavHostFragment).navController
        // 네비게이션 그래프에 있는 label이 툴바에 바인딩됨
        binding?.toolbar?.setupWithNavController(navigationController)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}