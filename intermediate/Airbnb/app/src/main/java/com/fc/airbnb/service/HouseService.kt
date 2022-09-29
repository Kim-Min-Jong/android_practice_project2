package com.fc.airbnb.service

import com.fc.airbnb.model.HouseDto
import retrofit2.Call
import retrofit2.http.GET


interface HouseService {

    @GET("")
    fun getHouseList(): Call<HouseDto>

}