package com.fc.musicstreaming

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.fc.musicstreaming.adapter.PlayListAdapter
import com.fc.musicstreaming.databinding.FragmentPlayerBinding
import com.fc.musicstreaming.model.MusicModel
import com.fc.musicstreaming.model.PlayerModel
import com.fc.musicstreaming.service.webCrawling
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import java.util.concurrent.TimeUnit

class PlayerFragment : Fragment(R.layout.fragment_player) {
    private var binding: FragmentPlayerBinding? = null
    private var model = PlayerModel()
    private lateinit var playListAdapter: PlayListAdapter
    private var mainActivity: MainActivity? = null
    private var player: ExoPlayer? = null
    private val updateSeekRunnable = Runnable{
        updateSeek()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPlayView()
        initPlayListButton()
        initPlayControlButtons()
        initSeekBar()
        initRecyclerView()
        getVideoListFromWeb()

    }

    private fun initSeekBar() {
        binding?.playListSeekBar?.setOnTouchListener { _, _ ->
            false
        }
        binding?.playerSeekBar?.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar) {
                player?.seekTo((p0.progress * 1000).toLong())
            }
        })
    }

    private fun initPlayView() {
        context?.let {
            player = ExoPlayer.Builder(it).build()
        }

        binding?.playerView?.player = player
        binding?.let { binding ->
            player?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        binding.playControlImageView.setImageResource(R.drawable.ic_baseline_pause_48)
                    } else {
                        binding.playControlImageView.setImageResource(R.drawable.ic_baseline_play_arrow_48)
                    }
                }

                // 미디어 아이템이 변하면 -- 음악이 바뀌면
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    // 해당 음악의 인덱스를 가져와
                    val newIndex = mediaItem?.mediaId ?: return
                    // 현재 인덱스를 바꿔주고
                    model.currentPosition = newIndex.toInt()
                    updatePlayerView(model.currentMusicModel())
                    // 바뀐 리스트를 리사이클러뷰에 적용
                    playListAdapter.submitList(model.getAdapterModels())
                }

                // 플레이어의 상태가 바뀔 때 실행되는 콜백 (재생, 일시정지.. 등)
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    updateSeek()
                }
            })
        }
    }

    private fun updateSeek() {
        val player = this.player ?: return
        val duration = if(player.duration >= 0) player.duration else 0
        val position = player.currentPosition

        // UI 업데이트
        updateSeekUi(duration, position)

        // 재생중이라면 1초뒤에 다시 재귀호출로 갱신, 재생중이 아니면 실행x
        view?.removeCallbacks(updateSeekRunnable)
        val state = player.playbackState
        if(state != Player.STATE_IDLE && state != Player.STATE_ENDED) {
            view?.postDelayed(updateSeekRunnable, 1000)
        }
    }

    private fun updateSeekUi(duration: Long, position: Long) {
        binding?.let{
            it.playListSeekBar.max = (duration / 1000).toInt()
            it.playListSeekBar.progress = (position / 1000).toInt()

            it.playerSeekBar.max = (duration / 1000).toInt()
            it.playerSeekBar.progress = (position / 1000).toInt()

            it.playTimeTextView.text = String.format("%02d:%02d",
                    TimeUnit.MINUTES.convert(position, TimeUnit.MILLISECONDS),
                (position / 1000) % 60
                )
            it.totalTimeTextView.text = String.format("%02d:%02d",
                TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS),
                (duration / 1000) % 60
            )
        }
    }

    // 재생목록 바인딩
   private fun updatePlayerView(currentMusicModel: MusicModel?) {
        currentMusicModel ?: return

        binding?.let{
            it.trackTextView.text = currentMusicModel.track
            it.artistTextView.text = currentMusicModel.artist
            Glide.with(it.coverImageView.context)
                .load(currentMusicModel.coverUrl)
                .into(it.coverImageView)

        }
    }

    private fun initRecyclerView() {
        playListAdapter = PlayListAdapter {
            // 음악 재생
            playMusic(it)
        }
        binding?.playListRecyclerView?.apply {
            adapter = playListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initPlayListButton() {
        binding?.playlistImageView?.setOnClickListener {
            //만약 서버에서 데이터가 다 불러오지 못했을 때 재생하지 않음
            if(model.currentPosition == -1)
                return@setOnClickListener


            // 플레이리스트 뷰 전환을 뷰그룹을의 visibility로 제어
            binding?.playerViewGroup?.isVisible = model.isWatchedPlayListView
            binding?.playListViewGroup?.isVisible = model.isWatchedPlayListView.not()

            model.isWatchedPlayListView = !model.isWatchedPlayListView
        }
    }

    private fun initPlayControlButtons() {
        binding?.playControlImageView?.setOnClickListener {
            val player = this.player ?: return@setOnClickListener

            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
        binding?.skipNextImageView?.setOnClickListener {
            val nextMusic = model.nextMusic() ?: return@setOnClickListener
            playMusic(nextMusic)
        }
        binding?.skipPrevImageView?.setOnClickListener {
            val prevMusic = model.prevMusic() ?: return@setOnClickListener
            playMusic(prevMusic)
        }
    }

    private fun getVideoListFromWeb() {
        Thread {
            val list = webCrawling()
            Log.e("MainActivity", list.toString())

            list.let {
                model = it.mapper()
                mainActivity?.runOnUiThread {
                    setMusicList(model.getAdapterModels())
                    playListAdapter.submitList(model.getAdapterModels())
                }
            }
        }.start()

    }

    private fun setMusicList(modelList: List<MusicModel>) {
        context?.let {
            // exoplayer에 미리 플레이리스트를 저장해놓음
            player?.addMediaItems(modelList.map { model ->
                MediaItem.Builder()
                    .setMediaId(model.id.toString())
                    .setUri(model.streamUrl)
                    .build()
            })
            player?.prepare()
        }
    }

    private fun playMusic(musicModel: MusicModel) {
        // 인덱스를 통해서 MediaItem의 리스트를 다음으로 바꿔가면서 재생할 수 있음
        // 인덱스는 현재 음악의 위치
        model.updateCurrentPosition(musicModel)
        player?.apply{
            seekTo(model.currentPosition, 0)
            play()
        }
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
        view?.removeCallbacks(updateSeekRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        view?.removeCallbacks(updateSeekRunnable)
        binding = null
        mainActivity = null
        player?.release()
        player = null
    }

    companion object {
        fun newInstance(): PlayerFragment {
            return PlayerFragment()
        }
    }
}