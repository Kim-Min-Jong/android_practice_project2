package com.fc.pomodorotimer

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private val remainMinutesTv: TextView by lazy{
        findViewById<TextView>(R.id.remainMinutesTv)
    }
    private val remainSecondsTv:TextView by lazy{
        findViewById<TextView>(R.id.remainSecondsTv)
    }
    private val seekBar: SeekBar by lazy{
        findViewById<SeekBar>(R.id.seekBar)
    }

    private var currentCountDownTimer: CountDownTimer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
    }

    override fun onPause() {
        super.onPause()
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null

    }

    override fun onDestroy() {
        super.onDestroy()
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null

    }
    private fun bindViews(){
        seekBar.setOnSeekBarChangeListener (
            object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if(p2)
                        updateRemainingTime(p1 * 60 * 1000L)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    currentCountDownTimer?.cancel()
                    currentCountDownTimer = null
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    // seek bar 가 null 이면 그냥 리턴
                    p0 ?: return

                    currentCountDownTimer = createCountDownTimer(p0.progress * 60 * 1000L)
                    currentCountDownTimer?.start()
                }

            }
        )
    }
    private fun createCountDownTimer(initialMillis: Long) =
        object: CountDownTimer(initialMillis, 1000L){
            override fun onTick(p0: Long) {
                // interval 마다 함수 호출 --> ui변경
                updateRemainingTime(p0)
                updateSeekBar(p0)
            }

            override fun onFinish() {
                updateRemainingTime(0)
                updateSeekBar(0)
            }
        }

    @SuppressLint("SetTextI18n")
    private fun updateRemainingTime(remainMillis: Long) {
        val remainSeconds = remainMillis / 1000

        remainMinutesTv.text = "%02d".format(remainSeconds / 60)
        remainSecondsTv.text = "%02d".format(remainSeconds % 60)
    }
    private fun updateSeekBar(remainMillis: Long) {
        seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }
}