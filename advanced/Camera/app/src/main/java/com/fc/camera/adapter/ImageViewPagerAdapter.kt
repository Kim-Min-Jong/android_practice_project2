package com.fc.camera.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fc.camera.databinding.ViewholderImageBinding
import com.fc.camera.extensions.loadCenterCrop

class ImageViewPagerAdapter(
    val uriList: MutableList<Uri>
) : RecyclerView.Adapter<ImageViewPagerAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ViewholderImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bindData(uriList[position])
    }

    override fun getItemCount(): Int = uriList.size

    inner class ImageViewHolder(
        private val binding: ViewholderImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(uri: Uri) = with(binding) {
            imageView.loadCenterCrop(uri.toString())
        }

    }

}