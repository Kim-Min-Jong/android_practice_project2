package com.fc.subwayarrivalinfo.domain

data class ArrivalInformation(
    val subway: Subway,
    val direction: String,
    val message: String,
    val destination: String,
    val updatedAt: String
)
