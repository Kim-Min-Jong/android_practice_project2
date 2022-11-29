package com.fc.camera

import android.app.Activity
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.fc.camera.adapter.ImageViewPagerAdapter
import com.fc.camera.databinding.ActivityImageListBinding
import com.fc.camera.util.PathUtil
import java.io.File
import java.io.FileNotFoundException

class ImageListActivity : AppCompatActivity() {
    private var binding: ActivityImageListBinding? = null
    private lateinit var adapter: ImageViewPagerAdapter

    //url list를 받을 리스트
    private val uriList by lazy<List<Uri>> { intent.getParcelableArrayListExtra(URI_LIST_KEY)!!}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageListBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initViews()
    }


    private fun initViews() = with(binding) {
        this?.let{
            setSupportActionBar(toolbar)
            setupImageList()
        }
    }

    private fun setupImageList() = with(binding) {
        this?.let {
            if(::adapter.isInitialized.not())
                adapter = ImageViewPagerAdapter(uriList as MutableList<Uri>)
            imageViewPager.adapter = adapter
            indicator.setViewPager(imageViewPager)
            // 뷰페이저의 페이지가 바뀌었을 때 실행되는 콜백 등록
            imageViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    toolbar.title = getString(R.string.images_page, position + 1, adapter.itemCount)
                }
            })
            deleteButton.setOnClickListener {
                removeImage(uriList[imageViewPager.currentItem])
            }
        }
    }

    private fun removeImage(uri: Uri) {
        try{
            val file = File(PathUtil.getPath(this, uri) ?: throw FileNotFoundException())
            // 해당 파일을 지우고
            file.delete()
            // viewpager에도 사라지게 만듦
            adapter.uriList.let {
                val imageList = it
                imageList.remove(uri)
                adapter.uriList = imageList
                adapter.notifyDataSetChanged()
            }
            // 스캔을 통해 삭제를 된 것을 스캔해서 갤러리에도 삭제가 된것을 알려줌
            MediaScannerConnection.scanFile(this, arrayOf(file.path), arrayOf("image/jpeg"), null)
            // indicator도 하단 원 갯수 반영
            binding?.indicator?.setViewPager(binding?.imageViewPager)

            // 삭제할 것이 없으면 액티비티 끝냄
            if(adapter.uriList.isEmpty()) {
                finish()
            }
        } catch(e: FileNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "이미지가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
    companion object {
        private const val URI_LIST_KEY = "uriList"
        fun newIntent(activity: Activity, uriList: List<Uri>) =
            Intent(activity, ImageListActivity::class.java).apply {
                putExtra(URI_LIST_KEY, ArrayList<Uri>().apply {
                    uriList.forEach {
                        add(it)
                    }
                })
            }
    }
}