package com.fc.voicerecorder

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView

class CountUpView(
    context: Context,
    attrs: AttributeSet?= null
): AppCompatTextView(context, attrs) {
    private var timeStamp = 0L
    private val countUpAction: Runnable = object: Runnable{
        override fun run() {
            val currentTimeStamp = SystemClock.elapsedRealtime()
            val time = ((currentTimeStamp - timeStamp)/1000L).toInt()
            updateCountTime(time)

            handler?.postDelayed(this, 1000L)
        }
    }
    fun startCountUp(){
        timeStamp = SystemClock.elapsedRealtime()
        handler?.post(countUpAction)
    }
    fun stopCountUp(){
        handler?.removeCallbacks(countUpAction)
    }
    private fun updateCountTime(countTimeSeconds: Int){
        val minutes = countTimeSeconds / 60
        val seconds = countTimeSeconds % 60

        text = "%02%d:%02d".format(minutes, seconds)
    }
}