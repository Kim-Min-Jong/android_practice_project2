package com.fc.subwayarrivalinfo.data.repository

import com.fc.subwayarrivalinfo.domain.Station
import kotlinx.coroutines.flow.Flow

interface StationRepository {

    // observing 할수 있는 flow 리스트 객체
    val stations: Flow<List<Station>>

    // refresh 하여 새로운 역이 있는 지 확인하고 적용
    suspend fun refreshStations()
}