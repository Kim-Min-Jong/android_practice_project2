package com.fc.airbnb.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fc.airbnb.databinding.ItemHouseDetailForViewpagerBinding
import com.fc.airbnb.model.HouseModel

class HouseViewPagerAdapter(val itemClicked: (HouseModel) -> Unit): ListAdapter<HouseModel, HouseViewPagerAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemHouseDetailForViewpagerBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(houseModel: HouseModel){
            binding.titleTextView.text = houseModel.title
            binding.priceTextView.text = houseModel.price

            binding.root.setOnClickListener{
                itemClicked(houseModel)
            }
            Glide.with(binding.thumbnailIamgeView.context)
                .load(houseModel.imageUrl)
                .into(binding.thumbnailIamgeView)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHouseDetailForViewpagerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
    companion object {

        val diffUtil = object : DiffUtil.ItemCallback<HouseModel>() {
            override fun areItemsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem == newItem
            }

        }
    }


}