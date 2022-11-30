package com.fc.usedtrade.home

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.fc.usedtrade.databinding.ActivityAddArticleBinding
import com.fc.usedtrade.util.DBKey.Companion.DB_ARTICLES
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class AddArticleActivity : AppCompatActivity() {
    private var binding: ActivityAddArticleBinding? = null
    private lateinit var getGalleryImageLauncher: ActivityResultLauncher<Intent>
    private var selectedUri: Uri? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }
    private val articleDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_ARTICLES)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initImageLauncher()

        binding?.imageAddButton?.setOnClickListener {
            when {
                // 권한 확인
                ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // 권한이 부여되었을때 갤러리에서 사진을 선택하는 기능
                    navigatePhotos()
                }
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) -> {
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

        binding?.submitButton?.setOnClickListener {
            val title = binding?.titleEditText?.text.toString()
            val content = binding?.contentEditText?.text.toString()
            val sellerId = auth.currentUser?.uid.orEmpty()
            showProgress()
            if (selectedUri != null) {
                val photoUri = selectedUri ?: return@setOnClickListener
                uploadPhoto(photoUri,
                    successHandler = { uri ->
                        uploadArticle(sellerId, title, content, uri)
                    },
                    errorHandler = {
                        Toast.makeText(this, "사진을 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        hideProgress()
                    })
            } else{
                uploadArticle(sellerId, title, content, "")
            }


        }
    }

    private fun uploadArticle(sellerId: String, title: String, content: String, imageUrl: String) {
        val model = ArticleModel(sellerId, title, System.currentTimeMillis(), content, imageUrl)
        articleDB.push().setValue(model)
        hideProgress()
        finish()
    }

    private fun uploadPhoto(photoUri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = "${System.currentTimeMillis()}.png"

        // storage 도 child를 통해 하위 디렉토리식으로 연결 할 수 있음
        storage.reference.child("article/photo").child(fileName)
            .putFile(photoUri).addOnCompleteListener{
                if(it.isSuccessful){
                    // 스토리지에 성공적으로 넣었을 시 다시 그 넣어진 url을 가져와 successhandler를 실행
                    storage.reference.child("article/photo").child(fileName).downloadUrl
                        .addOnSuccessListener { uri ->
                            // 성공하면 핸들러로 가서 uri를 포함한 믈픔을 realtime db에 저장함
                            successHandler(uri.toString())
                        }.addOnFailureListener {
                            // 실패시 uri없이 db에 물품정보 저장
                            errorHandler()
                        }
                }  else{
                    errorHandler()
                }
            }
    }

    private fun initImageLauncher() {
        getGalleryImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val uri = result.data?.data // 선택한 이미지의 주소(상대경로)
                    if (uri != null) {
                        binding?.photoImageView?.setImageURI(uri)
                        selectedUri = uri
                    } else {
                        Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }
            }
    }

    private fun showPermissionContextPopUp() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("중고거래 앱에서 사진을 불러오기위해 권한이 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000
                )
            }.setNegativeButton("취소") { _, _ -> }
            .create()
            .show()
    }

    // SAF(Storage Access Framework) with content provider
    private fun navigatePhotos() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        //  deprecated      startActivityForResult()
        getGalleryImageLauncher.launch(intent)
    }

    private fun showProgress() {
        binding?.progressBar?.isVisible = true
    }
    private fun hideProgress() {
        binding?.progressBar?.isVisible = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 부여됨
                    navigatePhotos()
                } else {
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                //
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}