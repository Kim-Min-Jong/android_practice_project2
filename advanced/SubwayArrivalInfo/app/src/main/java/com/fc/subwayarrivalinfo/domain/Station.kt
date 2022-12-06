package com.fc.subwayarrivalinfo.domain

// 역 데이터 객체 정의
data class Station(
    val name: String,
    val isFavorited: Boolean,
    val connectedSubways: List<Subway>
)