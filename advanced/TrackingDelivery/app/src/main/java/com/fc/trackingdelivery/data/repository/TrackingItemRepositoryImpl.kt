package com.fc.trackingdelivery.data.repository

import com.fc.trackingdelivery.data.api.SweetTrackerApi
import com.fc.trackingdelivery.data.db.TrackingItemDao
import com.fc.trackingdelivery.data.entity.TrackingInformation
import com.fc.trackingdelivery.data.entity.TrackingItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class TrackingItemRepositoryImpl(
    private val trackerApi: SweetTrackerApi,
    private val trackingItemDao: TrackingItemDao,
    private val dispatcher: CoroutineDispatcher
) : TrackingItemRepository {

    // 옵저빙 가능한 데이터 (db의 변경 자동동 반영
    override val trackingItems: Flow<List<TrackingItem>> =
        trackingItemDao.allTrackingItems()
            .distinctUntilChanged()
            .flowOn(dispatcher)

    // 추적정보 가져오기 구현
    override suspend fun getTrackingItemInformation(): List<Pair<TrackingItem, TrackingInformation>> =
        withContext(dispatcher) {
            // 먼저 db에서 정보를 다 가져옴
            trackingItemDao.getAll()
                // 데이터 변환
                .mapNotNull { trackingItem ->

                    // db에 담겨있는 item의 요소를 이용하여 api call을 해서 추적 정보를 가져옴옴
                    val relatedTrackingInfo = trackerApi.getTrackingInformation(
                        trackingItem.company.code,
                        trackingItem.invoice
                    ).body()

                    // 에러메시지가 있으면 null 저장
                    if (!relatedTrackingInfo!!.errorMessage.isNullOrBlank()) {
                        null
                    } else {
                        trackingItem to relatedTrackingInfo
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

    override suspend fun saveTrackingItem(trackingItem: TrackingItem) = withContext(dispatcher) {
        // 추적 정보를 가져옴
        val trackingInformation = trackerApi.getTrackingInformation(
            trackingItem.company.code,
            trackingItem.invoice
        ).body()

        // 추적 정보에 에러메세지가 있으면 에러메시지를 포함한 에러 발생시킴
        if (!trackingInformation!!.errorMessage.isNullOrBlank()) {
            throw RuntimeException(trackingInformation.errorMessage)
        }

        // 아니면 db에 추적 정보를 저장장
        trackingItemDao.insert(trackingItem)
    }
}