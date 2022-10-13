package com.fc.youtubeapp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fc.youtubeapp.adapter.VideoAdapter
import com.fc.youtubeapp.databinding.FragmentPlayerBinding
import com.fc.youtubeapp.dto.VideoDto
import com.fc.youtubeapp.service.VideoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.abs

class PlayerFragment : Fragment(R.layout.fragment_player) {
    private var binding: FragmentPlayerBinding? = null
    private lateinit var videoAdapter: VideoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPlayerBinding.bind(view)

        initMotionLayoutEvent()
        initRecyclerView()

        getVideoList()
    }

    private fun initRecyclerView() {
        videoAdapter = VideoAdapter{ url, title ->
            play(url,title)
        }

        binding?.fragmentRecyclerView?.apply{
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initMotionLayoutEvent() {
        binding?.playerMotionLayout?.setTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                binding?.let {
                    (activity as MainActivity).also {
                        println(progress)
                        it.binding?.mainMotionLayout?.progress = abs(progress)
                    }
                }
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {}

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }

        })
    }
    private fun getVideoList() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).also{
            it.listVideos().enqueue(object: Callback<VideoDto> {
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

    fun play(url:String, title:String) {
        binding?.let{
            // 모션레이아웃을 end상태로 트랜지션함
            it.playerMotionLayout.transitionToEnd()
            it.bottomTitleTextView.text = title
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}