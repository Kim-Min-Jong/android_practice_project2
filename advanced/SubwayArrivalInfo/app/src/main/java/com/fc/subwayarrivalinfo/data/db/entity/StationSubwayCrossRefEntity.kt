package com.fc.subwayarrivalinfo.data.db.entity

import androidx.room.Entity

// 각 호선에 겹치는 역을 나타내는 엔티티
// 역이름과 호선을 외래키로 받아 기본키로 사용
@Entity(primaryKeys = ["stationName", "subwayId"])
data class StationSubwayCrossRefEntity(
    val stationName: String,
    val subwayId: Int
)