package com.fc.voicerecorder

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    private val recordButton: RecordButton by lazy {
        findViewById<RecordButton>(R.id.recordBtn)
    }

    private var state = State.BEFORE_RECORDING
    private val requiredPermissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestAudioPermission()
        initViews()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermissionGranted =
            requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        if(!audioRecordPermissionGranted){
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

    companion object{
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }
}