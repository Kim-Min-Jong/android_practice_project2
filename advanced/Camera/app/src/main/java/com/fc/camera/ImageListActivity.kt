package com.fc.camera

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.fc.camera.adapter.ImageViewPagerAdapter
import com.fc.camera.databinding.ActivityImageListBinding

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