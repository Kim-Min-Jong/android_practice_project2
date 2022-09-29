package com.fc.airbnb.service

import com.fc.airbnb.model.HouseDto
import retrofit2.Call
import retrofit2.http.GET


interface HouseService {

    @GET("/v3/3d904949-1252-48a8-ac63-58583c2964a1")
    fun getHouseList(): Call<HouseDto>

}