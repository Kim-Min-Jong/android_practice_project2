package com.fc.subwayarrivalinfo.data.db

import androidx.room.*
import com.fc.subwayarrivalinfo.data.db.entity.StationEntity
import com.fc.subwayarrivalinfo.data.db.entity.StationSubwayCrossRefEntity
import com.fc.subwayarrivalinfo.data.db.entity.StationWithSubwaysEntity
import com.fc.subwayarrivalinfo.data.db.entity.SubwayEntity
import kotlinx.coroutines.flow.Flow


// DAO
// 역, 노선 등을 저장하고
// 역 노선 사이의 상관관계도 저장 (환승)
@Dao
interface StationDao {

    @Transaction
    @Query("SELECT * FROM StationEntity")
    fun getStationWithSubways(): Flow<List<StationWithSubwaysEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStations(station: List<StationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubways(subways: List<SubwayEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossReferences(reference: List<StationSubwayCrossRefEntity>)

    @Transaction
    suspend fun insertStationSubways(stationSubways: List<Pair<StationEntity, SubwayEntity>>) {
        insertStations(stationSubways.map { it.first })
        insertSubways(stationSubways.map { it.second })
        insertCrossReferences(
            stationSubways.map { (station, subway) ->
                StationSubwayCrossRefEntity(
                    station.stationName,
                    subway.subwayId
                )
            }
        )
    }
}