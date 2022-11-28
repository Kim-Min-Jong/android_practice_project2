package com.fc.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fc.camera.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    // 카메라를 실행시키기위한 서비스
    private lateinit var cameraExecutor: ExecutorService

    private val cameraMainExecutor by lazy { ContextCompat.getMainExecutor(this) }

    // 카메라 얻기 - 카메라 얻어오면 이후 실행리스너 등록
    private val cameraProviderFuture by lazy {
        ProcessCameraProvider.getInstance(this)
    }

    private lateinit var imageCapture: ImageCapture

    // 실행 리스너 매니저
    private val displayManager by lazy {
        getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    private var displayId = -1

    private var camera: Camera? = null

    // 실행 리스너
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(p0: Int) = Unit
        override fun onDisplayRemoved(p0: Int) = Unit
        override fun onDisplayChanged(displayId: Int) {
            if (this@MainActivity.displayId == displayId) {
                // 이미지 캡쳐 구현 (현재 상태에 대해)
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // 권한이 있으면 카메라 시작
        if (allPermissionsGranted()) {
            binding?.viewFinder?.let {
                startCamera(it)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    // 카메라 권한 요청
    // all 확장함수 - 모든 원소가 조건을 만족하면 true 아니면 false
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 카메라 실행 함수
    private fun startCamera(viewFinder: PreviewView) {
        // 디스플레이가 변경이 되었을때(회전, 화면이동 등) 감지하고 변화를 주기 위해서 등록
        displayManager.registerDisplayListener(displayListener, null)
        // 카메라를 실행시킬 쓰레드를 만들어줌
        cameraExecutor = Executors.newSingleThreadExecutor()
        binding?.viewFinder?.postDelayed({
            // 현재 viewFinder의 id를 displayId로 지정
            displayId = viewFinder.display.displayId
            bindCameraUseCase()
        }, 10)
    }

    private fun bindCameraUseCase() = with(binding) {
        this?.let {
            // 화면이 가로인지 세로인지
            val rotation = viewFinder.display.rotation
            // 기본 후면카메라 세팅
            val cameraSelector = CameraSelector.Builder().requireLensFacing(LENS_FACING).build()

            // 카메라 동작
            cameraProviderFuture.addListener({
                // 카메라 가져오기
                val cameraProvider = cameraProviderFuture.get()
                // 화면 만들기
                val preview = Preview.Builder().apply {
                    // 4:3 비율
                    setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    // 회전 지정
                    setTargetRotation(rotation)
                }.build()

                // 이미지 캡쳐를 위한 객체
                val imageCaptureBuilder = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .setTargetRotation(rotation)
                    .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                imageCapture = imageCaptureBuilder.build()

                try{
                    // 기존에 카메라가 바인딩이 되어있을 수도 있는데 그것을 모두 제거
                    cameraProvider.unbindAll()
                    // 카메라 객체 가져옴
                    camera = cameraProvider.bindToLifecycle(
                        this@MainActivity,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                    preview.setSurfaceProvider(viewFinder.surfaceProvider)
                } catch (e: Exception) { e.printStackTrace() }
            }, cameraMainExecutor)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (allPermissionsGranted()) {
            binding?.viewFinder?.let {
                startCamera(it)
            }
        } else {
            Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        // 권한 요청 값 상수
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

        // 기본 후면 카메리 사용
        private val LENS_FACING = CameraSelector.LENS_FACING_BACK
    }
}