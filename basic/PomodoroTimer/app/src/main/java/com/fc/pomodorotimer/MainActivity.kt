package com.fc.pomodorotimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
    }

    private fun bindViews(){
        seekBar.setOnSeekBarChangeListener (
            object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    remainMinutesTv.text = "%02d".format(p1)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }

            }
        )
    }
}