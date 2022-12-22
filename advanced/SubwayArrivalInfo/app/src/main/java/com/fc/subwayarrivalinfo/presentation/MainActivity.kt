package com.fc.subwayarrivalinfo.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.fc.subwayarrivalinfo.R
import com.fc.subwayarrivalinfo.databinding.ActivityMainBinding
import com.fc.subwayarrivalinfo.extensions.toGone
import com.fc.subwayarrivalinfo.extensions.toVisible
import com.fc.subwayarrivalinfo.presentation.stationarrivals.StationArrivalsFragmentArgs

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private val navigationController by lazy {
        (supportFragmentManager.findFragmentById(R.id.mainNavigationHostContainer) as NavHostFragment).navController
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initViews()
        bindViews()
    }
    override fun onSupportNavigateUp(): Boolean {
        return navigationController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun initViews() {
        setSupportActionBar(binding?.toolbar)
        // 액션바와 네비게이션 연동
        setupActionBarWithNavController(navigationController)
    }

    private fun bindViews() {
        navigationController.addOnDestinationChangedListener { _, destination, argument ->
            if (destination.id == R.id.station_arrivals_dest) {
                // 역 클릭때마다 툴바에 역이름 바인딩시킴
                title = StationArrivalsFragmentArgs.fromBundle(argument!!).station.name
                binding?.toolbar?.toVisible()
            } else {
                binding?.toolbar?.toGone()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}