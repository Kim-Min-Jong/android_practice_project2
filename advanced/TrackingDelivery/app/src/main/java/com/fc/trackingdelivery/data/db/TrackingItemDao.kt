package com.fc.trackingdelivery.data.db

import androidx.room.*
import com.fc.trackingdelivery.data.entity.TrackingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackingItemDao {

    // 옵저버블한 데이터 (변경사항(추가,삭제 시) 있을 시 택배 목록을 자동 반영하려고)
    @Query("SELECT * FROM TrackingItem")
    fun allTrackingItems(): Flow<List<TrackingItem>>

    // 모든 택배 아이템 가져오기 (변경사항(추가, 삭제)을 옵저빙 못하기 때문에 수동으로 해주어야함)
    @Query("SELECT * FROM TrackingItem")
    suspend fun getAll(): List<TrackingItem>

    // 새로운 택배 추가
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: TrackingItem)

    // 운송장 삭제
    @Delete
    suspend fun delete(item: TrackingItem)
}
