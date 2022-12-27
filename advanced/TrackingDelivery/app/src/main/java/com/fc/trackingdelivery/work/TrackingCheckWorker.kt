package com.fc.trackingdelivery.work

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fc.trackingdelivery.R
import com.fc.trackingdelivery.data.entity.Level
import com.fc.trackingdelivery.data.repository.TrackingItemRepository
import com.fc.trackingdelivery.presentation.MainActivity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

// 워커
class TrackingCheckWorker (
    // 코루틴워커 상속을 위한 기본 변수
     val context: Context,
     workerParams: WorkerParameters,
     // 주입해주어야 할것
    private val trackingItemRepository: TrackingItemRepository,
    private val dispatcher: CoroutineDispatcher
) : CoroutineWorker(context, workerParams) {

    // 실제 쓰레딩하는 로직(워커)
    override suspend fun doWork(): Result = withContext(dispatcher) {
        try {
            // 추적 정보를 가져오는데, 배송출발한 정보만 가져옴
            val startedTrackingItems = trackingItemRepository.getTrackingItemInformation()
                .filter { it.second.level == Level.START }

            // 비어있지 않으면 알림을 준비함
            if (startedTrackingItems.isNotEmpty()) {
                // 알림 채널 생성
                createNotificationChannelIfNeeded()

                // 배송 출발 목록 중, 대표로 첫번째 것을 가져옴
                val representativeItem = startedTrackingItems.first()

                // noti manager를 통해 휴대폰으로 알림을 보냄
                NotificationManagerCompat
                    .from(context)
                    .notify(
                        NOTIFICATION_ID,
                        createNotification(
                            "${representativeItem.second.itemName}(${representativeItem.first.company.name}) " +
                                    "외 ${startedTrackingItems.size - 1}건의 택배가 배송 출발하였습니다."
                        )
                    )
            }

            // 로직이 성공하면 (작업 성공) 성공을 반환
            Result.success()
        } catch (exception: Exception) {
            // 에러 시 실패를 반환
            Result.failure()
        }
    }

    // 채널 생성 함수
    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_DESCRIPTION

            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    // 알람 생성 함수 (인텐트-펜딩인텐트) 구현으로 알람 클릭 시 앱 실행
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createNotification(
        message: String?
    ): Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT)

        // builder 패턴을 통해 noti 생성
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_local_shipping_24)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }

    companion object {
        private const val CHANNEL_NAME = "Daily Tracking Updates"
        private const val CHANNEL_DESCRIPTION = "매일 배송 출발한 상품을 알려줍니다."
        private const val CHANNEL_ID = "Channel Id"
        private const val NOTIFICATION_ID = 101
    }
}
