package com.fc.trackingdelivery.data.repository

import com.fc.trackingdelivery.data.entity.TrackingInformation
import com.fc.trackingdelivery.data.entity.TrackingItem

interface TrackingItemRepository {
    // 추적정보 가져오기 프레임
    suspend fun getTrackingItemInformation(): List<Pair<TrackingItem, TrackingInformation>>
}