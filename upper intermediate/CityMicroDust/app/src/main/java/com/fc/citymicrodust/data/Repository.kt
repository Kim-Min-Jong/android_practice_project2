package com.fc.citymicrodust.data

import com.fc.citymicrodust.BuildConfig
import com.fc.citymicrodust.data.model.airquality.MeasuredValue
import com.fc.citymicrodust.data.model.monitoringstation.MonitoringStation
import com.fc.citymicrodust.data.service.AirKoreaApiService
import com.fc.citymicrodust.data.service.KakaoLocalApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


//레트로핏 구현체
object Repository {
    private val kakaoLocalApiService: KakaoLocalApiService by lazy{
        Retrofit.Builder()
            .baseUrl(Url.KAKAO_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            // 로그 찍기 위해
            .client(buildOkHttpClient())
            .build()
            // 원래 서비스 연결 시 서비스 파일을 명시해야하지만 Kotlin Extension에서 미리 정의된 함수로 바로 연결 가능
            .create()
    }
    private val airKoreaApiService: AirKoreaApiService by lazy{
        Retrofit.Builder()
            .baseUrl(Url.AIR_KOREA_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            // 로그 찍기 위해
            .client(buildOkHttpClient())
            .build()
            // 원래 서비스 연결 시 서비스 파일을 명시해야하지만 Kotlin Extension에서 미리 정의된 함수로 바로 연결 가능
            .create()
    }

    private fun buildOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                //인터셉터로 로그를 가져와 찍되 레벨 설정을 하여 로그내용의 수준을 결정함
                HttpLoggingInterceptor().apply {
                    level = if(BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()

    suspend fun getNearbyMonitoringStation(latitude: Double, longitude: Double) : MonitoringStation? {
        val tmCoordinate = kakaoLocalApiService.getTmCoordinates(longitude, latitude)
            .body()?.documents?.firstOrNull()

        val tmX = tmCoordinate?.x
        val tmY = tmCoordinate?.x

        // 좌표기준 여러 측정소 중에서 가장 가까운 측정소 찾기
        return airKoreaApiService
            .getNearbyMonitoringStation(tmX!!, tmY!!)
            .body()
            ?.response
            ?.body
            ?.monitoringStations
            ?.minByOrNull { it?.tm ?: Double.MAX_VALUE  }
    }

    suspend fun getLatestAirQualityData(stationName: String) : MeasuredValue? =
        airKoreaApiService.getRealtimeAirQualities(stationName)
            .body()
            ?.response
            ?.body
            ?.measuredValues
            ?.firstOrNull()
}