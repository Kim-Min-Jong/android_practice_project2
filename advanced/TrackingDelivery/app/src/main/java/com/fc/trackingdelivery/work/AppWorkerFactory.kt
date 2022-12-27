package com.fc.trackingdelivery.work

import androidx.work.DelegatingWorkerFactory
import com.fc.trackingdelivery.data.repository.TrackingItemRepository
import kotlinx.coroutines.CoroutineDispatcher

// 실제 앱에 주입하는 팩토리 생성
class AppWorkerFactory (
    trackingItemRepository: TrackingItemRepository,
    dispatcher: CoroutineDispatcher
) : DelegatingWorkerFactory() {

    init {
        addFactory(TrackingCheckWorkerFactory(trackingItemRepository, dispatcher))
    }
}