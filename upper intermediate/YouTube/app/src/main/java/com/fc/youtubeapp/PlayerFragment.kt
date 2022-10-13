package com.fc.youtubeapp

import android.net.Uri
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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.abs

class PlayerFragment : Fragment(R.layout.fragment_player) {
    private var binding: FragmentPlayerBinding? = null
    private lateinit var videoAdapter: VideoAdapter
    private var player: ExoPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPlayerBinding.bind(view)

        initMotionLayoutEvent()
        initRecyclerView()
        initPlayer()
        initControlButton()
        getVideoList()
    }
    private fun initControlButton() {
        binding?.bottomPlayerControlBtn?.setOnClickListener {
            val player = this.player ?: return@setOnClickListener

            if(player.isPlaying){
                player.pause()
            } else {
                player.play()
            }
        }
    }
    private fun initPlayer() {
        context?.let {
            player = ExoPlayer.Builder(it).build()
        }

        binding?.playerView?.player = player
        binding?.let{
            player?.addListener(object: Player.Listener{
                // 플레이 여부가 바뀔 때 마다 실행되는 콜백  isPlaying, true - 실행  false - 멈춤
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if(isPlaying){
                        it.bottomPlayerControlBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    } else {
                        it.bottomPlayerControlBtn.setImageResource(R.drawable.ic_baseline_pause_24)
                    }
                }
            })
        }
    }

    private fun initRecyclerView() {
        videoAdapter = VideoAdapter { url, title ->
            play(url, title)
        }

        binding?.fragmentRecyclerView?.apply {
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

        retrofit.create(VideoService::class.java).also {
            it.listVideos().enqueue(object : Callback<VideoDto> {
                override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
                    if (response.isSuccessful.not()) {
                        Log.d("MainActivity", " Response fail1")
                        return
                    }
                    response.body()?.let { videoDto ->
                        Log.d("MainActivity", " Response success")
                        videoAdapter.submitList(videoDto.videos)
                    }
                }

                override fun onFailure(call: Call<VideoDto>, t: Throwable) {
                    Log.d("MainActivity", " Response fail2")

                }

            })
        }
    }

    fun play(url: String, title: String) {
        context?.let {
            val dataSourceFactory = DefaultDataSource.Factory(it)
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
            player?.setMediaSource(mediaSource)
            player?.prepare()
            player?.play()
        }

        binding?.let {
            // 모션레이아웃을 end상태로 트랜지션함
            it.playerMotionLayout.transitionToEnd()
            it.bottomTitleTextView.text = title
        }
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        player?.release()
    }
}