package com.fc.musicstreaming

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.fc.musicstreaming.adapter.PlayListAdapter
import com.fc.musicstreaming.databinding.FragmentPlayerBinding
import com.fc.musicstreaming.model.MusicModel
import com.fc.musicstreaming.model.PlayerModel
import com.fc.musicstreaming.service.webCrawling
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player

class PlayerFragment : Fragment(R.layout.fragment_player) {
    private var binding: FragmentPlayerBinding? = null
    private var model = PlayerModel()
    private lateinit var playListAdapter: PlayListAdapter
    private var mainActivity: MainActivity? = null
    private var player: ExoPlayer? = null


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
        initRecyclerView()
        getVideoListFromWeb()

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

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    val newIndex = mediaItem?.mediaId ?: return
                    model.currentPosition = newIndex.toInt()
                    playListAdapter.submitList(model.getAdapterModels())
                }
            })
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

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        mainActivity = null
        player = null
    }

    companion object {
        fun newInstance(): PlayerFragment {
            return PlayerFragment()
        }
    }
}