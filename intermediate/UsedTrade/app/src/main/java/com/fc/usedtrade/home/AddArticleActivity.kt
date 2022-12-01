package com.fc.usedtrade.home

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.fc.usedtrade.adapter.PhotoListAdapter
import com.fc.usedtrade.databinding.ActivityAddArticleBinding
import com.fc.usedtrade.photo.CameraActivity
import com.fc.usedtrade.photo.ImageListActivity.Companion.URI_LIST_KEY
import com.fc.usedtrade.util.DBKey.Companion.DB_ARTICLES
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class AddArticleActivity : AppCompatActivity() {
    private var binding: ActivityAddArticleBinding? = null
    private lateinit var getGalleryImageLauncher: ActivityResultLauncher<Intent>
    private var imageUriList: ArrayList<Uri> = arrayListOf()
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }
    private val articleDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_ARTICLES)
    }

    private val photoListAdapter = PhotoListAdapter { uri -> removePhoto(uri) }
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.e("launcher", it.resultCode.toString())
            if (it.resultCode == Activity.RESULT_OK) {
                val uriList = it.data?.getParcelableArrayListExtra<Uri>(URI_LIST_KEY)
                //val data = it.data?.data
                if (uriList != null) {
                    //imageUriList.add(data!!)  xxxx 에러발생 uri 타입이 이상한듯
                    uriList.forEach { uri ->
                        imageUriList.add(uri)
                    }
                    // 리사이클러뷰 반영
                    //photoListAdapter.setPhotoList(uriList)
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.(uri없음)", Toast.LENGTH_SHORT).show()
                }
                // 리사이클러뷰 반영
                photoListAdapter.setPhotoList(imageUriList)


            } else {
                Toast.makeText(this, "사진을 가져오지 못했습니다.(인텐트 실패)", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initImageLauncher()
        initViews()
    }

    private fun initViews() = with(binding) {
        this?.let {
            photoRecyclerView.adapter = photoListAdapter
            imageAddButton.setOnClickListener {
                showPictureUploadDialog()
            }

            submitButton.setOnClickListener {
                val title = titleEditText.text.toString()
                val content = contentEditText.text.toString()
                val sellerId = auth.currentUser?.uid.orEmpty()
                showProgress()
                if (imageUriList.isNotEmpty()) {
                    lifecycleScope.launch {
                        val results = uploadPhoto(imageUriList)
                        uploadArticle(sellerId, title, content, results.filterIsInstance<String>())
                    }
                } else {
                    uploadArticle(sellerId, title, content, listOf())
                }
            }
        }
    }


    private fun uploadArticle(sellerId: String, title: String, content: String, imageUrlList: List<String>) {
        val model = ArticleModel(sellerId, title, System.currentTimeMillis(), content, imageUrlList)
        articleDB.push().setValue(model)
        hideProgress()
        finish()
    }

    private suspend fun uploadPhoto(uriList: List<Uri>) = withContext(Dispatchers.IO) {
        // storage 도 child를 통해 하위 디렉토리식으로 연결 할 수 있음 -- > 비동기로 변경
//        val fileName = "${System.currentTimeMillis()}.png"
//        storage.reference.child("article/photo").child(fileName)
//            .putFile(photoUri).addOnCompleteListener {
//                if (it.isSuccessful) {
//                    // 스토리지에 성공적으로 넣었을 시 다시 그 넣어진 url을 가져와 successhandler를 실행
//                    storage.reference.child("article/photo").child(fileName).downloadUrl
//                        .addOnSuccessListener { uri ->
//                            // 성공하면 핸들러로 가서 uri를 포함한 믈픔을 realtime db에 저장함
//                            successHandler(uri.toString())
//                        }.addOnFailureListener {
//                            // 실패시 uri없이 db에 물품정보 저장
//                            errorHandler()
//                        }
//                } else {
//                    errorHandler()
//                }
//            }
        // 비동기로 storage에 이미지 저장 (여러 이미지 가능 List Type)
        val uploadDeferred: List<Deferred<Any>> = uriList.mapIndexed { index, uri ->
            lifecycleScope.async {
                try {
                    val fileName = "image${index}.png"
                    return@async storage.reference.child("article/photo").child(fileName)
                        .putFile(uri)
                        .await()
                        .storage
                        .downloadUrl
                        .await()
                        .toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@async Pair(uri, e)
                }
            }
        }
        return@withContext uploadDeferred.awaitAll()
    }

    private fun initImageLauncher() {
        getGalleryImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val uri = result.data?.data // 선택한 이미지의 주소(상대경로)
                    if (uri != null) {
                        //binding?.photoImageView?.setImageURI(uri)
                        imageUriList.add(uri)
                        photoListAdapter.setPhotoList(imageUriList)
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

    // 카메라 찍을 수 있ㄱ[
    private fun startCameraScreen() {
        val intent = Intent(this, CameraActivity::class.java)
        launcher.launch(intent)
    }


    private fun showProgress() {
        binding?.progressBar?.isVisible = true
    }

    private fun hideProgress() {
        binding?.progressBar?.isVisible = false
    }

    private fun checkExternalStoragePermission(uploadAction: () -> Unit) {
        when {
            // 권한 확인
            ContextCompat.checkSelfPermission(
                this@AddArticleActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 권한이 부여되었을때 갤러리에서 사진을 선택하는 기능
                uploadAction()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this@AddArticleActivity,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                // 교육용 팝업 확인후 권한 팝업 띄우는 기능
                showPermissionContextPopUp()
            }
            else -> {
                ActivityCompat.requestPermissions(
                    this@AddArticleActivity,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1000
                )
            }
        }
    }

    // 사진 등록시 카메라 / 갤러리 구분을 위한 다이얼로그 생성 함수
    private fun showPictureUploadDialog() {
        AlertDialog.Builder(this)
            .setTitle("사진 첨부")
            .setMessage("사진 첨부할 방식을 선택하세요")
            .setPositiveButton("카메라") { _, _ ->
                checkExternalStoragePermission {
                    startCameraScreen()
                }
            }.setNegativeButton("갤러리") { _, _ ->
                checkExternalStoragePermission {
                    navigatePhotos()
                }
            }
            .create()
            .show()
    }

    // 리사이클러뷰 이미지 지우기
    private fun removePhoto(uri: Uri) {
        imageUriList.remove(uri)
        photoListAdapter.setPhotoList(imageUriList)
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