package com.fc.gradingmovie.presentation.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fc.gradingmovie.databinding.ItemFeaturedMovieBinding
import com.fc.gradingmovie.databinding.ItemMovieBinding
import com.fc.gradingmovie.domain.model.FeaturedMovie
import com.fc.gradingmovie.domain.model.Movie
import com.fc.gradingmovie.extension.dip
import com.fc.gradingmovie.extension.toAbbreviatedString
import com.fc.gradingmovie.extension.toDecimalFormatString

class HomeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: List<DataItem> = emptyList()
    var onMovieClickListener: ((Movie) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            ITEM_VIEW_TYPE_SECTION_HEADER -> {
                TitleItemViewHolder(parent.context)
            }
            ITEM_VIEW_TYPE_FEATURED -> {
                FeaturedMovieItemViewHolder(
                    ItemFeaturedMovieBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            ITEM_VIEW_TYPE_ITEM -> {
                MovieItemViewHolder(
                    ItemMovieBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            else -> throw RuntimeException("알 수 없는 ViewType 입니다.")
        }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemValue = data[position].value
        when {
            holder is TitleItemViewHolder && itemValue is String -> {
                holder.bind(itemValue)
            }
            holder is FeaturedMovieItemViewHolder && itemValue is FeaturedMovie -> {
                holder.bind(itemValue)
            }
            holder is MovieItemViewHolder && itemValue is Movie -> {
                holder.bind(itemValue)
            }
            else -> throw RuntimeException("알 수 없는 ViewHolder 입니다.")
        }
    }

    override fun getItemViewType(position: Int): Int = when (data[position].value) {
        // 데이터의 타입따라 뷰 타입을 가져옴
        is String -> {
            ITEM_VIEW_TYPE_SECTION_HEADER
        }
        is FeaturedMovie -> {
            ITEM_VIEW_TYPE_FEATURED
        }
        else -> {
            ITEM_VIEW_TYPE_ITEM
        }
    }

    // 데이터 추가
    fun addData(featuredMovie: FeaturedMovie?, movies: List<Movie>) {
        val newData = mutableListOf<DataItem>()

        // 추천 영화가 있으면 아이템을 넣어주고
        featuredMovie?.let {
            newData += DataItem("🔥 요즘 핫한 영화") // ITEM_VIEW_TYPE_SECTION_HEADER
            newData += DataItem(it) // ITEM_VIEW_TYPE_FEATURED
        }

        // 나머지 영화를 넣어준다
        newData += DataItem("🍿 이 영화들은 보셨나요?") //ITEM_VIEW_TYPE_SECTION_HEADER
        newData += movies.map { DataItem(it) } // ITEM_VIEW_TYPE_ITEM

        // 최종 데이터 세팅
        data = newData
    }

    // 타이틀
    inner class TitleItemViewHolder(context: Context) : RecyclerView.ViewHolder(
        TextView(context).apply {
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.BLACK)
            setPadding(dip(12f), dip(6f), dip(12f), dip(6f))
        }
    ) {

        fun bind(item: String) {
            (itemView as? TextView)?.text = item
        }
    }

    // 추천 영화 상세
    inner class FeaturedMovieItemViewHolder(private val binding: ItemFeaturedMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // 클릭시
            binding.root.setOnClickListener {
                // 해당위치의 데이터가 FeaturedMovie타입인지 확인하고
                (data[adapterPosition].value as? FeaturedMovie)?.movie?.let {
                    // 클릭 리스너 실행
                    onMovieClickListener?.invoke(it)
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(item: FeaturedMovie) {
            Glide.with(binding.root)
                .load(item.movie.posterUrl)
                .into(binding.posterImageView)

            binding.scoreCountTextView.text = item.movie.numberOfScore?.toAbbreviatedString()
            binding.averageScoreTextView.text = item.movie.averageScore?.toDecimalFormatString("0.0")

            item.latestReview?.let { review ->
                binding.latestReviewLabelTextView.text =
                    if (review.userId.isNullOrBlank()) {
                        "🌟 따끈따끈한 후기"
                    } else {
                        "- ${review.userId.take(3)}*** -"
                    }

                binding.latestReviewTextView.text = "\"${review.content}\""
            }
        }
    }

    // 그외 영화 상세
    inner class MovieItemViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // 클릭시
            binding.root.setOnClickListener {
                // 해당위치의 데이터가 Movie타입인지 확인하고
                (data[adapterPosition].value as? Movie)?.let {
                    // 클릭 리스너 실행
                    onMovieClickListener?.invoke(it)
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(movie: Movie) {
            Glide.with(binding.root)
                .load(movie.posterUrl)
                .into(binding.posterImageView)

            movie.let {
                binding.titleTextView.text = it.title
                binding.additionalInformationTextView.text = "${it.releaseYear}·${it.country}"
            }
        }
    }

    data class DataItem(val value: Any)

    companion object {
        const val ITEM_VIEW_TYPE_SECTION_HEADER = 0
        const val ITEM_VIEW_TYPE_FEATURED = 1
        const val ITEM_VIEW_TYPE_ITEM = 2
    }
}