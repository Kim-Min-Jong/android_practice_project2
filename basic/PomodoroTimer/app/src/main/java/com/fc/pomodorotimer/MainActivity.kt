package com.fc.pomodorotimer

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private val remainMinutesTv: TextView by lazy {
        findViewById<TextView>(R.id.remainMinutesTv)
    }
    private val remainSecondsTv: TextView by lazy {
        findViewById<TextView>(R.id.remainSecondsTv)
    }
    private val seekBar: SeekBar by lazy {
        findViewById<SeekBar>(R.id.seekBar)
    }
    private val soundPool = SoundPool.Builder().build()
    private var currentCountDownTimer: CountDownTimer? = null

    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        initSounds()
    }

    override fun onPause() {
        super.onPause()
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.release()
    }

    private fun bindViews() {
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if (p2)
                        updateRemainingTime(p1 * 60 * 1000L)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    stopCountDown()
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    // seek bar 가 null 이면 그냥 리턴
                    p0 ?: return

                    if(seekBar.progress == 0) {
                        stopCountDown()
                    }else{
                        startCountDown()
                    }
                }
            }
        )
    }
    private fun stopCountDown() {
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()
    }
    private fun createCountDownTimer(initialMillis: Long) =
        object : CountDownTimer(initialMillis, 1000L) {
            override fun onTick(p0: Long) {
                // interval 마다 함수 호출 --> ui변경
                updateRemainingTime(p0)
                updateSeekBar(p0)
            }

            override fun onFinish() {
                completeCountDown()
            }
        }
    private fun startCountDown(){
        currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
        currentCountDownTimer?.start()

        tickingSoundId?.let {
            soundPool.play(it, 1f, 1f, 0, -1, 1f)
        }
    }
    private fun completeCountDown(){
        updateRemainingTime(0)
        updateSeekBar(0)

        soundPool.autoPause()
        bellSoundId?.let {
            soundPool.play(it, 1f, 1f, 0, 0, 1f)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateRemainingTime(remainMillis: Long) {
        val remainSeconds = remainMillis / 1000

        remainMinutesTv.text = "%02d'".format(remainSeconds / 60)
        remainSecondsTv.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(remainMillis: Long) {
        seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }

    private fun initSounds() {
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }
}