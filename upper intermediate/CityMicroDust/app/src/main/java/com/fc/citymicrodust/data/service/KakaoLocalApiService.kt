package com.fc.citymicrodust.data.service

import com.fc.citymicrodust.BuildConfig
import com.fc.citymicrodust.data.model.tmcoordinate.TmCoordinateResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


// 서버스 api 인터페이스
interface KakaoLocalApiService {
    @Headers("Authorization:KakaoAK ${BuildConfig.KAKAO_API_KEY}")
    @GET("/v2/local/geo/transcoord.json?output_coord=TM")
    suspend fun getTmCoordinates(
        @Query("x") longitude:Double,
        @Query("y") latitude: Double
    ): Response<TmCoordinateResponse>
}