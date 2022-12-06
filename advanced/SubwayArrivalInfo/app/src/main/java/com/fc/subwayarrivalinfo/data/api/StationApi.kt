package com.fc.subwayarrivalinfo.data.api

import com.fc.subwayarrivalinfo.data.db.entity.StationEntity
import com.fc.subwayarrivalinfo.data.db.entity.SubwayEntity

// 정보를 가져오는 인터페이스 - 추후 스토리지에서 가져올 때 재정의
interface StationApi {

    suspend fun getStationDataUpdatedTimeMillis(): Long

    suspend fun getStationSubways(): List<Pair<StationEntity, SubwayEntity>>
}