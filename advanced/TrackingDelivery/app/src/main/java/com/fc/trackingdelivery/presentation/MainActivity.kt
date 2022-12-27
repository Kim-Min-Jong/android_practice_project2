package com.fc.trackingdelivery.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.fc.trackingdelivery.R
import com.fc.trackingdelivery.databinding.ActivityMainBinding
import com.fc.trackingdelivery.work.TrackingCheckWorker
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView()
        initWorker()
    }

    private fun initView() {
        val navigationController =
            (supportFragmentManager.findFragmentById(R.id.mainNavigationHostContainer) as NavHostFragment).navController
        // 네비게이션 그래프에 있는 label이 툴바에 바인딩됨
        binding?.toolbar?.setupWithNavController(navigationController)
    }

    // 워커 초기화 (특정시간에 택배있으면 알림)
    private fun initWorker() {
        // 시작 시간  16시로 지정
        val workerStartTime = Calendar.getInstance()
        workerStartTime.set(Calendar.HOUR_OF_DAY, 16)

        // 현재 시간과 16시의 차이를 구함 (반복되는 워커를 실행하는데 최초로 시작하는데 딜레이를 주려고)
        // ex) 오후 1시에 앱을 켰는데, 오후 4시에 볼려면 3시간 이후에 알람이 와야 되는데, 이 3시간을 계산
        val initialDelay = workerStartTime.timeInMillis - System.currentTimeMillis()

        // PeriodicWorkRequestBuilder - 주기로 반복되는 워커 객체 생성 (1일)
        val dailyTrackingCheckRequest =
            PeriodicWorkRequestBuilder<TrackingCheckWorker>(1, TimeUnit.DAYS)
                // 딜레이 설정
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                // 실패 시 재시도 정책 (LINEAR - 몇 초뒤에)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

        // 워크매니저를 통해 워커 객체를 실행시켜 최종 워커를 만듦
        WorkManager.getInstance(this)
            // 주기적인 워크를 생성
            .enqueueUniquePeriodicWork(
                "DailyTrackingCheck",
                // 이미 워커가 존재하면 기존 것을 유지
                // 앱을 킬 때마다 initWorker가 실행되지만 새로 워커가 추가되지는 않는다. 이미 등록되어있기 때문에
                ExistingPeriodicWorkPolicy.KEEP,
                dailyTrackingCheckRequest
            )
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}