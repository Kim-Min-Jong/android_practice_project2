package com.fc.subwayarrivalinfo.data.repository

import com.fc.subwayarrivalinfo.data.api.StationApi
import com.fc.subwayarrivalinfo.data.api.StationArrivalsApi
import com.fc.subwayarrivalinfo.data.api.response.mapper.toArrivalInformation
import com.fc.subwayarrivalinfo.data.db.StationDao
import com.fc.subwayarrivalinfo.data.db.entity.StationSubwayCrossRefEntity
import com.fc.subwayarrivalinfo.data.db.entity.mapper.toStationEntity
import com.fc.subwayarrivalinfo.data.db.entity.mapper.toStations
import com.fc.subwayarrivalinfo.data.preference.PreferenceManager
import com.fc.subwayarrivalinfo.domain.ArrivalInformation
import com.fc.subwayarrivalinfo.domain.Station
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class StationRepositoryImpl(
    private val stationArrivalsApi: StationArrivalsApi,
    private val stationApi: StationApi,
    private val stationDao: StationDao,
    private val preferenceManager: PreferenceManager,
    private val dispatcher: CoroutineDispatcher
) : StationRepository {

    override val stations: Flow<List<Station>> =
        stationDao.getStationWithSubways()
            // 쿼리에 결과에 따른 값 변경이 다른데 이것을 적용시키지 않으면 모든 쿼리 실행할때마다 옵저빙을 해서 과도하게하는 문제가 생길 수 있다.
            // 이것을 방지하기 위해 선언하면 실행한 쿼리의 결과에 해당되는 값들만 옵저빙을 한다.
            .distinctUntilChanged()
            .map { stations -> stations.toStations().sortedByDescending { it.isFavorited } }
            .flowOn(dispatcher)

    override suspend fun refreshStations() = withContext(dispatcher) {
        // csv파일이 업데이트된 시점과 로컬디비가 업데이트 된 시점을 가져옴
        val fileUpdatedTimeMillis = stationApi.getStationDataUpdatedTimeMillis()
        val lastDatabaseUpdatedTimeMillis = preferenceManager.getLong(KEY_LAST_DATABASE_UPDATED_TIME_MILLIS)

        // 디비가 가져온 시간이 없거나(로컬디비 초기 상태), 파일 업데이트 시점이 디비보다 더 뒤라면 새로운 역 정보가 있다는 뜻으로
        // 정보 insert를 실행
        if (lastDatabaseUpdatedTimeMillis == null || fileUpdatedTimeMillis > lastDatabaseUpdatedTimeMillis) {
            stationDao.insertStationSubways(stationApi.getStationSubways())
            preferenceManager.putLong(KEY_LAST_DATABASE_UPDATED_TIME_MILLIS, fileUpdatedTimeMillis)
        }
    }

    override suspend fun getStationArrivals(stationName: String): List<ArrivalInformation> = withContext(dispatcher) {
        stationArrivalsApi.getRealtimeStationArrivals(stationName)
            .body()
            ?.realtimeArrivalList
            ?.toArrivalInformation()
            ?.distinctBy { it.direction }
            ?.sortedBy { it.subway }
            ?: throw RuntimeException("도착 정보를 불러오는 데에 실패했습니다.")
    }

    override suspend fun updateStation(station: Station) = withContext(dispatcher) {
        stationDao.updateStation(station.toStationEntity())
    }

    companion object {
        private const val KEY_LAST_DATABASE_UPDATED_TIME_MILLIS = "KEY_LAST_DATABASE_UPDATED_TIME_MILLIS"
    }
}