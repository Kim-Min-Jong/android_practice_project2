package com.fc.youtubeapp.service

import com.fc.youtubeapp.dto.VideoDto
import retrofit2.Call
import retrofit2.http.GET

interface VideoService {
    @GET("")
    fun listVideos(): Call<VideoDto>
}