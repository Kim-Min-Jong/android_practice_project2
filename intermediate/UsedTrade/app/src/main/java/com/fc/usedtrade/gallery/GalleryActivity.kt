package com.fc.usedtrade.gallery

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.fc.usedtrade.R
import com.fc.usedtrade.adapter.GalleryPhotoListAdapter
import com.fc.usedtrade.adapter.GridDividerDecoration
import com.fc.usedtrade.databinding.ActivityGalleryBinding

class GalleryActivity : AppCompatActivity() {
    private var binding: ActivityGalleryBinding? = null

    // 리사이클러뷰의 아이템을 클릭했을 떄 선택된 상태로 만듦
    private val adapter = GalleryPhotoListAdapter {
        viewModel.selectPhoto(it)
    }

    private val viewModel by viewModels<GalleryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initViews()
        viewModel.fetchData()
        observeState()
    }

    private fun initViews() = with(binding) {
        this?.let {
            recyclerView.adapter = adapter
            recyclerView.addItemDecoration(
                GridDividerDecoration(
                    this@GalleryActivity,
                    R.drawable.bg_frame_gallery
                )
            )
            confirmButton.setOnClickListener {
                viewModel.confirmCheckedPhotos()
            }
        }
    }

    private fun observeState() = viewModel.galleryStateLiveData.observe(this) {
        when (it) {
            is GalleryState.Loading -> handleLoading()
            is GalleryState.Success -> handleSuccess(it)
            is GalleryState.Confirm -> handleConfirm(it)
            else -> Unit
        }
    }

    private fun handleLoading() = with(binding) {
        this?.let {
            progressBar.isVisible = true
            recyclerView.isGone = true
        }
    }

    private fun handleSuccess(state: GalleryState.Success) = with(binding) {
        this?.let {
            progressBar.isGone = true
            recyclerView.isVisible = true
            adapter.setPhotoList(state.photoList)
        }
    }

    private fun handleConfirm(state: GalleryState.Confirm) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(URI_LIST_KEY, ArrayList(state.photoList.map { it.uri }))
        })
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        fun newIntent(activity: Activity) = Intent(activity, GalleryActivity::class.java)
        private const val URI_LIST_KEY = "uriList"
    }
}