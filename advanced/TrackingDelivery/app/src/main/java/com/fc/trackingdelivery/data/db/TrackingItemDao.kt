package com.fc.trackingdelivery.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fc.trackingdelivery.data.entity.TrackingItem

@Dao
interface TrackingItemDao {

    // 모든 택배 아이템 가져오기
    @Query("SELECT * FROM TrackingItem")
    suspend fun getAll(): List<TrackingItem>

    // 새로운 택배 추가
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: TrackingItem)
}
