package com.fc.trackingdelivery.data.repository

import com.fc.trackingdelivery.data.api.SweetTrackerApi
import com.fc.trackingdelivery.data.db.TrackingItemDao
import com.fc.trackingdelivery.data.entity.TrackingInformation
import com.fc.trackingdelivery.data.entity.TrackingItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class TrackingItemRepositoryImpl(
    private val trackerApi: SweetTrackerApi,
    private val trackingItemDao: TrackingItemDao,
    private val dispatcher: CoroutineDispatcher
) : TrackingItemRepository {
    // 추적정보 가져오기 구현
    override suspend fun getTrackingItemInformation(): List<Pair<TrackingItem, TrackingInformation>> = withContext(dispatcher) {
        // 먼저 db에서 정보를 다 가져옴
        trackingItemDao.getAll()
             // 데이터 변환
            .mapNotNull { trackingItem ->

                // db에 담겨있는 item의 요소를 이용하여 api call을 해서 추적 정보를 가져옴옴
               val relatedTrackingInfo = trackerApi.getTrackingInformation(
                    trackingItem.company.code,
                    trackingItem.invoice
                ).body()

                // 송장번호가 존재하면 Pair<TrackingItem, TrackingInformation>로 데이터를 만듦
                if (relatedTrackingInfo?.invoiceNo.isNullOrBlank()) {
                    null
                } else {
                    trackingItem to relatedTrackingInfo!!
                }
            }
            // 마지막으로 TrackingInformation의 레벨로 오름차순 정렬을 한 후 그걸 기준으로 시간 내림차순(-) 정렬함
            .sortedWith(
                compareBy(
                    { it.second.level },
                    { -(it.second.lastDetail?.time ?: Long.MAX_VALUE) }
                )
            )
    }
}