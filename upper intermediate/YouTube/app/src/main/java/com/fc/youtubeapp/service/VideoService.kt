package com.fc.youtubeapp.service

import com.fc.youtubeapp.dto.VideoDto
import retrofit2.Call
import retrofit2.http.GET

interface VideoService {
    @GET("v3/7470cd97-3da2-4cd9-a807-d03de7c7d731")
    fun listVideos(): Call<VideoDto>
}