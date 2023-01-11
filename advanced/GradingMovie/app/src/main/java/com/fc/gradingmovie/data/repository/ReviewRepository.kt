package com.fc.gradingmovie.data.repository

import com.fc.gradingmovie.domain.model.Review

interface ReviewRepository {
    // 영화 id로 그 영화의 최신 리뷰를 가져온다.
    suspend fun getLatestReview(movieId: String): Review?

    // api를 통해 리뷰를 가져온다.
    suspend fun getAllMovieReviews(movieId: String): List<Review>

    suspend fun getAllUserReviews(userId: String): List<Review>
}