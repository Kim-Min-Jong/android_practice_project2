package com.fc.citymicrodust.data.service

import com.fc.citymicrodust.BuildConfig
import com.fc.citymicrodust.data.model.airquality.AirQualityResponse
import com.fc.citymicrodust.data.model.monitoringstation.MonitoringStationsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AirKoreaApiService {

    @GET("/B552584/MsrstnInfoInqireSvc/getMsrstnList?serviceKey=${BuildConfig.AIR_KOREA_SERVICE_KEY}"
    + "&returnType=json")
    suspend fun getNearbyMonitoringStation(
        @Query("tmX") tmX: Double,
        @Query("tmY") tmY: Double
    ): Response<MonitoringStationsResponse>

    @GET("/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?serviceKey=${BuildConfig.AIR_KOREA_SERVICE_KEY}"
            + "&returnType=json"
            + "&dataTerm=DAILY"
            + "&ver=1.3")
    suspend fun getRealtimeAirQualities(
        @Query("stationName") stationName: String
    ): Response<AirQualityResponse>
}