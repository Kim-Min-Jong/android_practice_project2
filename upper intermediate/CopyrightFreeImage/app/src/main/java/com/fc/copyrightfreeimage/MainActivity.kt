package com.fc.copyrightfreeimage

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.fc.copyrightfreeimage.data.Repository
import com.fc.copyrightfreeimage.data.adapter.PhotoAdapter
import com.fc.copyrightfreeimage.data.models.PhotoResponse
import com.fc.copyrightfreeimage.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initViews()
        bindViews()

        // 안드로이드10이상일 때는 권한 없이 사용 가능하다.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            fetchRandomPhotos()
        } else {
            // 만드로이드10 미만이면 권한이 요구된다.
            requestStoragePermission()
        }
    }

    private fun initViews() {
        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = PhotoAdapter()
        }
    }

    private fun bindViews() {
        binding?.searchEditText?.setOnEditorActionListener { editText, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentFocus?.let {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)

                    it.clearFocus()
                }

                fetchRandomPhotos(editText.text.toString())
            }
            true
        }

        binding?.refreshLayout?.setOnRefreshListener {
            fetchRandomPhotos(binding?.searchEditText?.text.toString())
        }

        (binding?.recyclerView?.adapter as? PhotoAdapter)?.onClickPhoto = { photo ->
            showDownloadPhotoConfirmationDialog(photo)
        }
    }


    private fun fetchRandomPhotos(query: String? = null) {
        scope.launch {
            // 렌더링시 에러처리
            try {
                Repository.getRandomPhotos(query)?.let { photos ->

                    binding?.errorDescriptionTextView?.visibility = View.GONE
                    (binding?.recyclerView?.adapter as? PhotoAdapter)?.apply {
                        this.photos = photos
                        notifyDataSetChanged()
                    }
                }
                binding?.recyclerView?.visibility = View.VISIBLE
            } catch (e: Exception) {
                binding?.recyclerView?.visibility = View.INVISIBLE
                binding?.errorDescriptionTextView?.visibility = View.VISIBLE
            } finally {
                binding?.shimmerLayout?.visibility = View.GONE
                binding?.refreshLayout?.isRefreshing = false
            }
        }
    }

    private fun showDownloadPhotoConfirmationDialog(photo: PhotoResponse) {
        AlertDialog.Builder(this)
            .setMessage("사진을 저장하시겠습니까?")
            .setPositiveButton("저장") { dialog, which ->
                downloadPhoto(photo.urls?.full)
                dialog.dismiss()
            }.setNegativeButton("취소") { dialog, which ->

                dialog.dismiss()
            }.create()
            .show()
    }

    private fun downloadPhoto(photoUrl: String?) {
        photoUrl ?: return
        // 글라이드로 로딩 후 로딩 성공시 다운로드 로직 실행
        Glide.with(this)
            .asBitmap()
            .load(photoUrl)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(
                object: CustomTarget<Bitmap>(SIZE_ORIGINAL, SIZE_ORIGINAL) {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        saveBitmapToMediaStore(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) = Unit

                    override fun onLoadStarted(placeholder: Drawable?) {
                        super.onLoadStarted(placeholder)
                        Snackbar.make(binding?.root as View, "다운로드 중...", Snackbar.LENGTH_INDEFINITE).show()
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        Snackbar.make(binding?.root as View, "다운로드 실패...", Snackbar.LENGTH_SHORT).show()
                    }
                }
            )
    }

    // scope storage로 인한 MediaStore 분기처리가 요구됨 (안드로이드 10 기준)
    private fun saveBitmapToMediaStore(bitmap: Bitmap) {
        // 컨텐츠 리졸버를 통하여 미디어스토어의 설정
        val fileName = "${System.currentTimeMillis()}.jpg"
        val resolver = applicationContext.contentResolver
        val imageCollectionUrl =
            //10 이상일 경우 정해진 Volume들이 있음 VOLUME_EXTERNAL_PRIMARY- 읽고 쓰기 가능
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                //10이상이 아닐 경우 그냥 외부 URI 갖고 오면 됨
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            // (10이상일때) 이미지 저장시 긴 시간이 걸릴 수 있는데, 이 사이에 그 이미지 파일에 접근할 수도 있다.
            // 이것을 IS_PENDING 값을 1로 두면 막을 수 있고, 0이 되면 그때부터 접근할 수 있다.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val imageUri = resolver.insert(imageCollectionUrl, imageDetails)
        imageUri ?: return

        // 실제 저장 과정
        resolver.openOutputStream(imageUri).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)

        }

        // 다시 이미지의 접근 권한을 바꾸고 리졸버의 설정을 업데이트한다.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.clear()
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(imageUri, imageDetails, null, null)
        }

        Snackbar.make(binding?.root as View, "다운로드 완료", Snackbar.LENGTH_SHORT).show()
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val writeExternalStoragePermissionGranted =
            requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED

        if(writeExternalStoragePermissionGranted) {
            fetchRandomPhotos()
        } else {
            requestStoragePermission()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        scope.cancel()
    }

    companion object {
        private const val REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 101
    }
}