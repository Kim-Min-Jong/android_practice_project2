package com.fc.usedtrade.photo

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.core.impl.ImageOutputConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.fc.usedtrade.databinding.ActivityCameraBinding
import com.fc.usedtrade.extensions.loadCenterCrop
import com.fc.usedtrade.photo.ImageListActivity.Companion.URI_LIST_KEY
import com.fc.usedtrade.util.PathUtil
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private var binding: ActivityCameraBinding? = null

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

    // 화면이 회전한지 확인하는 전체 뷰
    private var root: View? = null

    // 캡쳐 중인지 아닌지
    private var isCapturing = false
    // 플래시 토글버튼 상태
    private var isFlashEnabled = false

    // 실행 리스너
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(p0: Int) = Unit
        override fun onDisplayRemoved(p0: Int) = Unit

        @SuppressLint("RestrictedApi")
        override fun onDisplayChanged(displayId: Int) {
            if (this@CameraActivity.displayId == displayId) {
                // 이미지 캡쳐 구현 (현재 회전 상태에 대해)
                if (::imageCapture.isInitialized && root != null)
                    imageCapture.targetRotation =
                        root?.display?.rotation ?: ImageOutputConfig.INVALID_ROTATION
            }
        }

    }

    // 이미지 관리할 리스트
    private val uriList = mutableListOf<Uri>()

    private var data: Intent? = null
    // 인텐트 넘겨줄 런쳐 onActivityResult(deprecated) 대응
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) {
            val data = it.data?.getParcelableArrayListExtra<Uri>(URI_LIST_KEY)

            setResult(Activity.RESULT_OK, Intent().apply{
                putExtra(URI_LIST_KEY, data)
            })
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        root = binding?.root
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

                try {
                    // 기존에 카메라가 바인딩이 되어있을 수도 있는데 그것을 모두 제거
                    cameraProvider.unbindAll()
                    // 카메라 객체 가져옴
                    camera = cameraProvider.bindToLifecycle(
                        this@CameraActivity,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                    preview.setSurfaceProvider(viewFinder.surfaceProvider)
                    bindCaptureListener()
                    bindZoomListener()
                    initFlashAndAddListener()
                    bindPreviewImageViewClickListener()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, cameraMainExecutor)
        }
    }

    // 줌인 아웃 기능 함수
    @SuppressLint("ClickableViewAccessibility")
    private fun bindZoomListener() = with(binding) {
        this?.let {
            // ScaleGestureDetector - 손가락으로 줌 제스쳐를 취했을 떄 얼마나 늘어나느냐 줄어드느냐 감지하는 리스너
            val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    // 현재 줌 비율
                    val currentZoomRatio = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1f
                    // 움직인 비율
                    val delta = detector.scaleFactor

                    // 현재 비율에서 움직인 비율을 곱하여 줌을 세팅
                    camera?.cameraControl?.setZoomRatio(currentZoomRatio * delta)
                    return true
                }
            }

            // 리스너 등록
            val scaleGestureDetector = ScaleGestureDetector(this@CameraActivity, listener)
            viewFinder.setOnTouchListener { _, motionEvent ->
                scaleGestureDetector.onTouchEvent(motionEvent)
                return@setOnTouchListener true
            }
        }
    }

    private fun bindCaptureListener() = with(binding) {
        this?.let {
            captureButton.setOnClickListener {
                if (isCapturing.not()) {
                    isCapturing = true
                    captureCamera()
                } else {

                }
            }
        }
    }

    // 플래시 초기화 및 리스너 등록
    private fun initFlashAndAddListener() = with(binding) {
        this?.let {
            val hasFlash = camera?.cameraInfo?.hasFlashUnit() ?: false
            flashSwitch.isGone = hasFlash.not()
            if (hasFlash) {
                flashSwitch.setOnCheckedChangeListener { _, isChecked ->
                    isFlashEnabled = isChecked
                }
            } else {
                isFlashEnabled = false
                flashSwitch.setOnCheckedChangeListener(null)
            }
        }
    }

    // 미리보기 이미지 클릭 시 미리보기 액티비티로 넘어가는 리스너
    private fun bindPreviewImageViewClickListener() = with(binding) {
        this?.let {
            previewImageVIew.setOnClickListener {
                launcher.launch(ImageListActivity.newIntent(this@CameraActivity, uriList))
            }
        }
    }

    // 이미지가 저장이 되었고, 다른 갤러리에 보여달라고 설정하는 함수
    private fun updateSavedImageContent() {
        contentUri?.let {
            isCapturing = try {
                val file = File(PathUtil.getPath(this, it) ?: throw FileNotFoundException())
                // file(이미지)를 스캔함
                MediaScannerConnection.scanFile(
                    this,
                    arrayOf(file.path),
                    arrayOf("image/jpeg"),
                    null
                )

                Handler(Looper.getMainLooper()).post {
                    binding?.previewImageVIew?.loadCenterCrop(url = it.toString(), corner = 4f)
                }
                uriList.add(it)
                flashLight(false)
                false
            } catch (e: Exception) {
                e.printStackTrace()
                flashLight(false)
                Toast.makeText(this, "파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }


    // 가장 최근에 저장된 이미지의 Uri
    private var contentUri: Uri? = null

    // 카메라 캡쳐하기
    private fun captureCamera() {
        // 앱이 실행되어 이미지 캡쳐 객체가 있어야하는데 없으면 바로 리턴
        if (::imageCapture.isInitialized.not()) return

        // 캡쳐 저장 시작
        // 파일 선언
        val photoFile = File(
            PathUtil.getOutputDirectory(this),
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.KOREA
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // 파일을 쓸 수 있는 옵션 지정 (ImageCapture)
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        // 플래시 체크시 켜기
        if(isFlashEnabled)
            flashLight(true)
        // 이미지 캡쳐를 캡쳐함 (사진 찍기)  - 찍고 저장될 떄의 콜백을 지정
        imageCapture.takePicture(
            outputFileOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                    contentUri = savedUri
                    updateSavedImageContent()
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    isCapturing = false
                    flashLight(false)
                }

            })
    }

    // 플래시 제어
    private fun flashLight(light: Boolean) {
        val hasFlash = camera?.cameraInfo?.hasFlashUnit() ?: false
        if(hasFlash) {
            camera?.cameraControl?.enableTorch(light)
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

        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}