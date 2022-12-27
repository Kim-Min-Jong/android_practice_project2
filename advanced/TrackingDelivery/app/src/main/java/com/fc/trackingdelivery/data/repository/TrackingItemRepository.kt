package com.fc.trackingdelivery.data.repository

import com.fc.trackingdelivery.data.entity.TrackingInformation
import com.fc.trackingdelivery.data.entity.TrackingItem
import kotlinx.coroutines.flow.Flow

interface TrackingItemRepository {

    // 변경을 반영하는 데이터
    val trackingItems: Flow<List<TrackingItem>>

    // 추적정보 가져오기 프레임
    suspend fun getTrackingItemInformation(): List<Pair<TrackingItem, TrackingInformation>>

    // 변경된 데이터를 저장
    suspend fun saveTrackingItem(trackingItem: TrackingItem)

    // 추적 상세정보를 가져옴 (단일 건)
    suspend fun getTrackingInformation(companyCode: String, invoice: String): TrackingInformation?

    // 삭제
    suspend fun deleteTrackingItem(trackingItem: TrackingItem)
}