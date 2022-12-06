package com.fc.subwayarrivalinfo.data.db.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

// 각 역이 어느 노선에 속해 있는지 저장하는 엔티티
data class StationWithSubwaysEntity(
    @Embedded val station: StationEntity,
    @Relation(
        parentColumn = "stationName",
        entityColumn = "subwayId",
        associateBy = Junction(StationSubwayCrossRefEntity::class)
    )
    val subways: List<SubwayEntity>
)