package com.fc.youtubeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.fc.youtubeapp.adapter.VideoAdapter
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
    private lateinit var videoAdapter: VideoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PlayerFragment())
            .commit()

        videoAdapter = VideoAdapter{ url, title ->
            // 프래그먼트 매니저에서 프래그먼트를 찾아서(find) 사용할 프래그먼트를 가져온 다음 형변환을 통해
            // 해당 프래그먼트에 접근할 수 있음음
            supportFragmentManager.fragments.find{ it is PlayerFragment }?.let{
                (it as PlayerFragment).play(url, title)
            }
        }

        binding?.mainRecyclerView?.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }

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
                    response.body()?.let{ videoDto ->
                        Log.d("MainActivity"," Response success")
                        videoAdapter.submitList(videoDto.videos)
                    }
                }

                override fun onFailure(call: Call<VideoDto>, t: Throwable) {
                    Log.d("MainActivity"," Response fail2")

                }

            })
        }
    }
}