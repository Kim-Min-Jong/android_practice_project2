package com.fc.subwayarrivalinfo.data.api

import com.fc.subwayarrivalinfo.data.db.entity.StationEntity
import com.fc.subwayarrivalinfo.data.db.entity.SubwayEntity
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await


// Firebase Storage 에서 data를 가져오는 api 함수를 정의한 클래스
class StationStorageApi(
    firebaseStorage: FirebaseStorage
) : StationApi {

    // firebase에서 station_data.csv 파일을 가져옴
    private val sheetReference = firebaseStorage.reference.child(STATION_DATA_FILE_NAME)

    // csv가 갱신된 최근 시간을 가져옴
    override suspend fun getStationDataUpdatedTimeMillis(): Long =
        sheetReference.metadata.await().updatedTimeMillis

    // csv 파일을 읽어 역, 노선 정보의 리스트로 바꿔 져옴
    override suspend fun getStationSubways(): List<Pair<StationEntity, SubwayEntity>> {
        val downloadSizeBytes = sheetReference.metadata.await().sizeBytes
        val byteArray = sheetReference.getBytes(downloadSizeBytes).await()

        return byteArray.decodeToString()
            .lines()
            .drop(1)
            .map { it.split(",") }
            .map { StationEntity(it[1]) to SubwayEntity(it[0].toInt()) }
    }

    companion object {
        private const val STATION_DATA_FILE_NAME = "station_data.csv"
    }
}