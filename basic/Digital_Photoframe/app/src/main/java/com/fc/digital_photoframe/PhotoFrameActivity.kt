package com.fc.digital_photoframe

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import java.util.*
import kotlin.concurrent.timer

class PhotoFrameActivity : AppCompatActivity() {
    private val photoList = mutableListOf<Uri>()

    private val photoImageView:ImageView by lazy {
        findViewById<ImageView>(R.id.photoImageView)
    }
    private val backgroundPhotoImageView:ImageView by lazy {
        findViewById<ImageView>(R.id.backgroundPhotoImageView)
    }

    private var currentPosition = 0
    private var timer: Timer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_frame_activiry)
        Log.d("PhotoFrame", "onCreate!!!")
        getPhotoUriFromIntent()
    }

    private fun getPhotoUriFromIntent(){
        val size = intent.getIntExtra("photoListSize", 0)
        for(i in 0..size){
            intent.getStringExtra("photo$i")?.let{
                photoList.add(Uri.parse(it))
            }
        }
    }

    // 시각에 따라 이미지를 넘겨주기 위해 타이머 생성
    private fun startTimer(){
        // 기본적으로 타이머는 UI쓰레드가 아님
        timer = timer(period=5000){
            // 실행할 문장
            runOnUiThread{
                Log.d("PhotoFrame", "5초가 지나감 !!")
                val current = currentPosition
                val next = if(photoList.size <= currentPosition + 1) 0 else currentPosition + 1

                backgroundPhotoImageView.setImageURI(photoList[current])
                photoImageView.alpha = 0f
                photoImageView.setImageURI(photoList[next])
                photoImageView.animate()
                    .alpha(1.0f)
                    .setDuration(1000)
                    .start()

                currentPosition = next
            }
        }
    }

    override fun onStop() {
        super.onStop()

        Log.d("PhotoFrame", "onStop!!! timer cancel")
        timer?.cancel()
    }


    override fun onStart() {
        super.onStart()

        Log.d("PhotoFrame", "onStart!!! timer start")
        startTimer()
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("PhotoFrame", "onDestroy!!! timer cancel")
        timer?.cancel()
    }

}