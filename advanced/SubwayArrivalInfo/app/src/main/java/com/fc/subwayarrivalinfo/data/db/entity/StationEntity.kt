package com.fc.subwayarrivalinfo.data.db.entity

import androidx.room.*

// 각 역이름과 즐겨찾기가 되어있는지 확인하는 엔티티
@Entity
data class StationEntity(
    @PrimaryKey val stationName: String,
    val isFavorited: Boolean = false
)
