package com.fc.digital_photoframe

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val addPhotoButton: Button by lazy {
        findViewById<Button>(R.id.addPhotoButton)
    }
    private val startPhotoFrameModeButton: Button by lazy {
        findViewById<Button>(R.id.startPhotoFrameModeButton)
    }

    private lateinit var getGalleryImageLauncher:ActivityResultLauncher<Intent>

    private val ivList:List<ImageView> by lazy {
        mutableListOf<ImageView>().apply {
            add(findViewById<ImageView>(R.id.iv1))
            add(findViewById<ImageView>(R.id.iv2))
            add(findViewById<ImageView>(R.id.iv3))
            add(findViewById<ImageView>(R.id.iv4))
            add(findViewById<ImageView>(R.id.iv5))
            add(findViewById<ImageView>(R.id.iv6))
        }
    }
    private val imageUriList: MutableList<Uri> = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        getGalleryImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri = result.data?.data // 선택한 이미지의 주소(상대경로)
                    if(uri != null){
                        if(imageUriList.size == 6){
                            Toast.makeText(this,"사진이 꽉 찼습니다.",Toast.LENGTH_SHORT).show()
                            return@registerForActivityResult
                        }
                        imageUriList.add(uri)
                        ivList[imageUriList.size-1].setImageURI(uri)

                    } else{
                        Toast.makeText(this,"사진을 불러오지 못했습니다.",Toast.LENGTH_SHORT).show()
                        return@registerForActivityResult
                    }
                }
            }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAddPhotoButton()
        initStartPhotoFrameModeButton()
    }
    private fun initStartPhotoFrameModeButton() {
        startPhotoFrameModeButton.setOnClickListener {
            val intent = Intent(this, PhotoFrameActivity::class.java)
            imageUriList.forEachIndexed { index, uri ->
                intent.putExtra("photo$index", uri.toString())
            }
            intent.putExtra("photoListSize", imageUriList.size)
            startActivity(intent)
        }
    }
    private fun initAddPhotoButton() {
        addPhotoButton.setOnClickListener {
            when{
                // 권한 확인
                ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // 권한이 부여되었을때 갤러리에서 사진을 선택하는 기능
                    navigatePhotos()
                }
                ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    // 교육용 팝업 확인후 권한 팝업 띄우는 기능
                    showPermissionContextPopUp()
                }
                else -> {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000
                    )
                }
            }
        }
    }


    // 권한 요청의 결과를 반환하는 메소드
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            1000 -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // 권한 부여됨
//                    navigatePhotos()
                } else{
                    Toast.makeText(this,"권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else ->{
                //
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showPermissionContextPopUp() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("전자액자 앱에서 사진을 불러오기위해 권한이 필요합니다.")
            .setPositiveButton("동의허기"){ _, _ ->
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000
                )
            }.setNegativeButton("취소하기"){_,_ -> }
    }

    // SAF(Storage Access Framework) with content provider
    private fun navigatePhotos() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
//  deprecated      startActivityForResult()
        getGalleryImageLauncher.launch(intent)
    }



}