package com.fc.placesearchmap.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fc.placesearchmap.R
import com.fc.placesearchmap.databinding.ViewholderSearchResultItemBinding
import com.fc.placesearchmap.model.SearchResultEntity

class SearchRecyclerAdapter():
    RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder>() {
    private var searchResultList: List<SearchResultEntity> = listOf()
    lateinit var searchResultClickListener: (SearchResultEntity) -> Unit

    inner class ViewHolder(
        private val binding: ViewholderSearchResultItemBinding,
        val searchResultClickListener: (SearchResultEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(data: SearchResultEntity) = with(binding) {
            textTextView.text = data.fullAddress
            subtextTextView.text = data.name
        }

        fun bindViews(data: SearchResultEntity) {
            binding.root.setOnClickListener {
                searchResultClickListener(data)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ViewholderSearchResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), searchResultClickListener)
    }

    override fun getItemViewType(position: Int): Int = R.layout.viewholder_search_result_item

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(searchResultList[position])
        holder.bindViews(searchResultList[position])
    }

    override fun getItemCount(): Int = searchResultList.size

    fun setSearchResultList(searchResultList: List<SearchResultEntity>, searchResultClickListener: (SearchResultEntity) -> Unit) {
        this.searchResultList = searchResultList
        this.searchResultClickListener = searchResultClickListener
        notifyDataSetChanged()
    }
}