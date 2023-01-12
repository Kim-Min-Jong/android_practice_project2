package com.fc.gradingmovie.presentation.reviews

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fc.gradingmovie.domain.model.Movie
import com.fc.gradingmovie.domain.model.Review
import com.fc.gradingmovie.extension.toAbbreviatedString
import com.fc.gradingmovie.extension.toDecimalFormatString
import com.fc.gradingmovie.databinding.ItemMovieInformationBinding
import com.fc.gradingmovie.databinding.ItemMyReviewBinding
import com.fc.gradingmovie.databinding.ItemReviewBinding
import com.fc.gradingmovie.databinding.ItemReviewFormBinding
import com.google.android.material.chip.Chip

class MovieReviewsAdapter(private val movie: Movie) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var myReview: Review? = null

    var reviews: List<Review> = emptyList()

    // 클릭 리스너
    var onReviewSubmitButtonClickListener: ((content: String, score: Float) -> Unit)? = null
    var onReviewDeleteButtonClickListener: ((Review) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        // 뷰 타입에 따라 리뷰를 보여줄지 영화 정보를 보여줄지
        // 헤더 - 영화 정보
        // 그 외 - 리뷰 정보
        when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> {
                MovieInformationViewHolder(
                    ItemMovieInformationBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            ITEM_VIEW_TYPE_ITEM -> {
                ReviewViewHolder(parent)
            }
            // 리뷰 작성 시 폼 보여주는 타입
            ITEM_VIEW_TYPE_REVIEW_FORM -> {
                ReviewFormViewHolder(
                    ItemReviewFormBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            // 내 리뷰 보여주는 폼 타입
            ITEM_VIEW_TYPE_MY_REVIEW -> {
                MyReviewViewHolder(
                    ItemMyReviewBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            else -> throw RuntimeException("알 수 없는 ViewType 입니다.")
        }

    // 맨위에 영화 정보가 한개 있기 때문에 인덱스를 늘려줌 ( 내 리뷰도 보여주기 떄문에 1 더 추가)
    override fun getItemCount(): Int = 2 + reviews.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int){
        when (holder) {
            is MovieInformationViewHolder -> {
                holder.bind(movie)
            }
            is ReviewViewHolder -> {
                // 늘려준 인덱스 때문에 indexout error를 방지하기 위해 2 뺴줌
                holder.bind(reviews[position - 2])
            }
            is MyReviewViewHolder -> {
                myReview ?: return
                holder.bind(myReview!!)
            }
            is ReviewFormViewHolder -> Unit
            else -> throw RuntimeException("알 수 없는 ViewHolder 입니다.")
        }
    }
    override fun getItemViewType(position: Int): Int =
        when (position) {
            0 -> ITEM_VIEW_TYPE_HEADER
            1 -> {
                if (myReview == null) {
                    ITEM_VIEW_TYPE_REVIEW_FORM
                } else {
                    ITEM_VIEW_TYPE_MY_REVIEW
                }
            }
            else -> ITEM_VIEW_TYPE_ITEM
        }

    inner class MovieInformationViewHolder(private val binding: ItemMovieInformationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: Movie) {
            Glide.with(binding.root)
                .load(item.posterUrl)
                .into(binding.posterImageView)

            // 영화 정보 바인딩
            item.let {
                binding.averageScoreTextView.text =
                    "평점 ${it.averageScore?.toDecimalFormatString("0.0")} (${it.numberOfScore?.toAbbreviatedString()})"
                binding.titleTextView.text = it.title
                binding.additionalInformationTextView.text = "${it.releaseYear}·${it.country}"
                binding.relationsTextView.text = "감독: ${it.director}\n출연진: ${it.actors}"
                // 모든 칩그룹을 없애고 (초기화)
                binding.genreChipGroup.removeAllViews()
                // 장르를 칩그룹에 추가
                it.genre?.split(" ")?.forEach { genre ->
                    binding.genreChipGroup.addView(
                        Chip(binding.root.context).apply {
                            isClickable = false
                            text = genre
                        }
                    )
                }
            }
        }
    }

    inner class ReviewViewHolder(
        parent: ViewGroup,
        private val binding: ItemReviewBinding = ItemReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: Review) {
            item.let {
                binding.authorIdTextView.text = "${it.userId?.take(3)}***"
                binding.scoreTextView.text = it.score?.toDecimalFormatString("0.0")
                binding.contentsTextView.text = "\"${it.content}\""
            }
        }
    }
    @SuppressLint("SetTextI18n")
    inner class ReviewFormViewHolder(private val binding: ItemReviewFormBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            // submit 클릭 시
            binding.submitButton.setOnClickListener {
                // 리스너 호출
                onReviewSubmitButtonClickListener?.invoke(
                    // 리뷰 텍스트와
                    binding.reviewFieldEditText.text.toString(),
                    // 평점을 전달함
                    binding.ratingBar.rating
                )
            }
            // 텍스트가 입력될때마나
            binding.reviewFieldEditText.addTextChangedListener { editable ->
                // 현재 입력 수를 보여주고
                binding.contentLimitTextView.text = "(${editable?.length ?: 0}/50)"
                // 5글자 이상 작성하면 버튼을 활성화
                binding.submitButton.isEnabled = (editable?.length ?: 0) >= 5
            }
        }
    }

    inner class MyReviewViewHolder(private val binding: ItemMyReviewBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            // 삭제 클릭 리스너
            binding.deleteButton.setOnClickListener {
                // 삭제 리스너 호출
                onReviewDeleteButtonClickListener?.invoke(myReview!!)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(item: Review) {
            item.let {
                binding.scoreTextView.text = it.score?.toDecimalFormatString("0.0")
                binding.contentsTextView.text = "\"${it.content}\""
            }
        }
    }
    companion object {
        const val ITEM_VIEW_TYPE_HEADER = 0
        const val ITEM_VIEW_TYPE_ITEM = 1
        const val ITEM_VIEW_TYPE_REVIEW_FORM = 2
        const val ITEM_VIEW_TYPE_MY_REVIEW = 3
    }
}