package com.fc.subwayarrivalinfo.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


// 각 호선 엔티티
@Entity
data class SubwayEntity(
    @PrimaryKey val subwayId: Int,
)