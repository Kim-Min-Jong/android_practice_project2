package com.fc.placesearchmap.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fc.placesearchmap.R
import com.fc.placesearchmap.databinding.ItemSearchResultBinding

class SearchRecyclerAdapter(val clickListener:(Any)->Unit):
    RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder>() {
    private var searchResultList: List<Any> = listOf()

    inner class ViewHolder(private val binding: ItemSearchResultBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Any) = with(binding){
            textTextView.text = "제목"
            subtextTextView.text = "부제목"
            itemView.setOnClickListener {
                clickListener(Any())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(Any())
    }

    override fun getItemCount(): Int = 10

}