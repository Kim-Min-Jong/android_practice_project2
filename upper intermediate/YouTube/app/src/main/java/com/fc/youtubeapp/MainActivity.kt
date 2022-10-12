package com.fc.youtubeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.fc.youtubeapp.databinding.ActivityMainBinding
import com.fc.youtubeapp.dto.VideoDto
import com.fc.youtubeapp.service.VideoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PlayerFragment())
            .commit()
        getVideoList()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun getVideoList() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).also{
            it.listVideos().enqueue(object: Callback<VideoDto>{
                override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
                    if(response.isSuccessful.not()){
                        Log.d("MainActivity"," Response fail1")
                        return
                    }
                    response.body()?.let{
                        Log.d("MainActivity"," Response success")
                    }
                }

                override fun onFailure(call: Call<VideoDto>, t: Throwable) {
                    Log.d("MainActivity"," Response fail2")

                }

            })
        }
    }
}