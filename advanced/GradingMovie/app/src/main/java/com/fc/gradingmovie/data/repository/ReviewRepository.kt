package com.fc.gradingmovie.data.repository

import com.fc.gradingmovie.domain.model.Review

interface ReviewRepository {
    // 영화 id로 그 영화의 최신 리뷰를 가져온다.
    suspend fun getLatestReview(movieId: String): Review?

    // api를 통해 영화리뷰를 가져온다.
    suspend fun getAllMovieReviews(movieId: String): List<Review>

    // api를 통해 유저리뷰를 가져온다.
    suspend fun getAllUserReviews(userId: String): List<Review>

    // api를 통해 리뷰를 추가한다.
    suspend fun addReview(review: Review): Review

    // api를 통해 리뷰를 삭제한다.
    suspend fun removeReview(review: Review)
}