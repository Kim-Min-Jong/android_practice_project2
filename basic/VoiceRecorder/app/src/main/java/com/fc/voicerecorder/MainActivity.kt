package com.fc.voicerecorder

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    private val recordTimeView:CountUpView by lazy {
        findViewById<CountUpView>(R.id.recordTimeTv)
    }
    private val visualizerView: SoundVisualizerView by lazy{
        findViewById<SoundVisualizerView>(R.id.vv)
    }

    private val recordButton: RecordButton by lazy {
        findViewById<RecordButton>(R.id.recordBtn)
    }
    private val resetButton: Button by lazy {
        findViewById<Button>(R.id.resetBtn)
    }

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private val recordingPath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }

    // 커스텀 setter 지정시 초기화 해두면 작동하지 않음 메소드로 따로 조작해야함
    private var state = State.BEFORE_RECORDING
        set(value) {
            field = value
            resetButton.isEnabled = (value == State.AFTER_RECORDING) || (value == State.ON_PLAYING)
            recordButton.updateIconWithState(field)
        }
    private val requiredPermissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestAudioPermission()
        initViews()
        bindViews()
        initVariable()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermissionGranted =
            requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        if (!audioRecordPermissionGranted) {
            finish()
        }
    }

    private fun requestAudioPermission() {
        ActivityCompat.requestPermissions(
            this,
            requiredPermissions,
            REQUEST_RECORD_AUDIO_PERMISSION
        )
    }

    private fun initViews() {
        recordButton.updateIconWithState(state)
    }

    private fun startRecording() {
        recorder = MediaRecorder(this).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC) // 마이크에 접근
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP) // 출력 포맷 지정
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) // 인코더 지정
            setOutputFile(recordingPath) // 파일의 저장 경로
            prepare()  // 녹음 준비 완료
        }
        recorder?.start()
        visualizerView.startVisualizing(false)
        recordTimeView.startCountUp()
        state = State.ON_RECORDING

    }

    private fun stopRecording() {
        recorder?.run {
            stop()
            release()
        }
        recorder = null
        visualizerView.stopVisualizing()
        recordTimeView.stopCountUp()
        state = State.AFTER_RECORDING
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            setDataSource(recordingPath)
            prepare()
        }
        player?.start()
        visualizerView.startVisualizing(true)
        recordTimeView.startCountUp()
        state = State.ON_PLAYING
    }

    private fun stopPlaying() {
        player?.release()
        player = null
        visualizerView.stopVisualizing()
        recordTimeView.stopCountUp()
        state = State.AFTER_RECORDING
    }

    private fun bindViews() {
        visualizerView.onRequestCurrentAmplitude = {
            recorder?.maxAmplitude ?: 0

        }
        resetButton.setOnClickListener {
            stopPlaying()
            state = State.BEFORE_RECORDING
        }
        recordButton.setOnClickListener {
            when (state) {
                State.BEFORE_RECORDING -> {
                    startRecording()
                }
                State.ON_RECORDING -> {
                    stopRecording()
                }
                State.AFTER_RECORDING -> {
                    startPlaying()
                }
                State.ON_PLAYING -> {
                    stopPlaying()
                }
            }
        }
    }

    private fun initVariable() {
        state = State.BEFORE_RECORDING
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }

    override fun onDestroy() {
        super.onDestroy()
        player = null
        recorder = null
    }
}