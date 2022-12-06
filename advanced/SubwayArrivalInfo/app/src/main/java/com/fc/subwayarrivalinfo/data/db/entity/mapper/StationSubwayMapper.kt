package com.fc.subwayarrivalinfo.data.db.entity.mapper

import com.fc.subwayarrivalinfo.data.db.entity.StationWithSubwaysEntity
import com.fc.subwayarrivalinfo.data.db.entity.SubwayEntity
import com.fc.subwayarrivalinfo.domain.Station
import com.fc.subwayarrivalinfo.domain.Subway

fun StationWithSubwaysEntity.toStation() = Station(
    name = station.stationName,
    isFavorited = station.isFavorited,
    connectedSubways = subways.toSubways()
)

fun List<StationWithSubwaysEntity>.toStations() = map { it.toStation() }

fun List<SubwayEntity>.toSubways(): List<Subway> = map { Subway.findById(it.subwayId) }