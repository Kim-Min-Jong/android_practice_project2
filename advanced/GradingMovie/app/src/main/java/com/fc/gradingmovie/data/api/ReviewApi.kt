package com.fc.gradingmovie.data.api

import com.fc.gradingmovie.domain.model.Review

interface ReviewApi {
    // fireStore에서 최신리뷰를 가져온다.
    suspend fun getLatestReview(movieId: String): Review?

    // 모든 리뷰를 가져온다.
    suspend fun getAllReviews(movieId: String): List<Review>
}