package com.fc.trackingdelivery.data.api

import com.fc.trackingdelivery.BuildConfig
import com.fc.trackingdelivery.data.entity.ShippingCompanies
import com.fc.trackingdelivery.data.entity.TrackingInformation
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SweetTrackerApi {

    // 추적 정보 가져오는 api
    @GET("api/v1/trackingInfo?t_key=${BuildConfig.SWEET_TRACKER_API_KEY}")
    suspend fun getTrackingInformation(
        @Query("t_code") companyCode: String,
        @Query("t_invoice") invoice: String
    ): Response<TrackingInformation>

    // 회사 목록 가져오는 api
    @GET("api/v1/companylist?t_key=${BuildConfig.SWEET_TRACKER_API_KEY}")
    suspend fun getShippingCompanies(): Response<ShippingCompanies>

    // 택배사 추천하는 api
    @GET("api/v1/recommend?t_key=${BuildConfig.SWEET_TRACKER_API_KEY}")
    suspend fun getRecommendShippingCompanies(
        @Query("t_invoice") invoice: String
    ): Response<ShippingCompanies>
}