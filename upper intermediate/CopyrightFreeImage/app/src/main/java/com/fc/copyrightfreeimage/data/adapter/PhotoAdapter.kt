package com.fc.copyrightfreeimage.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.fc.copyrightfreeimage.R
import com.fc.copyrightfreeimage.data.models.PhotoResponse
import com.fc.copyrightfreeimage.databinding.ItemPhotoBinding

class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    var photos: List<PhotoResponse> = emptyList()
    var onClickPhoto: (PhotoResponse) -> Unit = {}

    inner class ViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init{
            binding.root.setOnClickListener {
                onClickPhoto(photos[adapterPosition])
            }
        }

        fun bind(photo: PhotoResponse) {
            // 이미지 조정을 위해 뷰들의 값 불러오기
            val dimensionRatio = photo.height / photo.width.toFloat()
            val targetWidth = binding.root.resources.displayMetrics.widthPixels -
                    (binding.root.paddingStart + binding.root.paddingEnd)
            val targetHeight = (targetWidth * dimensionRatio).toInt()

            binding.contentContainer.layoutParams =
                binding.contentContainer.layoutParams.apply {
                    height = targetHeight
                }

            Glide.with(binding.root)
                .load(photo.urls?.regular)
                .thumbnail(
                    Glide.with(binding.root)
                        .load(photo.urls?.thumb)
                        .transition(DrawableTransitionOptions.withCrossFade())
                )
                .override(targetWidth, targetHeight)
                .into(binding.photoImageView)

            Glide.with(binding.root)
                .load(photo.user?.profileImageUrls?.small)
                .placeholder(R.drawable.shape_profile_placeholder)
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.profileImageView)

            if (photo.user?.name.isNullOrBlank()) {
                binding.authorTextView.visibility = View.GONE
            } else {
                binding.authorTextView.visibility = View.VISIBLE
                binding.authorTextView.text = photo.user?.name
            }

            if (photo.description.isNullOrBlank()) {
                binding.descriptionTextView.visibility = View.GONE
            } else {
                binding.descriptionTextView.visibility = View.VISIBLE
                binding.descriptionTextView.text = photo.description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount(): Int = photos.size
}