package com.fc.subwayarrivalinfo.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// 역 데이터 객체 정의
@Parcelize
data class Station(
    val name: String,
    val isFavorited: Boolean,
    val connectedSubways: List<Subway>
): Parcelable