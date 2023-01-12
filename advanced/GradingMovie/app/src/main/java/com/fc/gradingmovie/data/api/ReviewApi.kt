package com.fc.gradingmovie.data.api

import com.fc.gradingmovie.domain.model.Review

interface ReviewApi {
    // fireStore에서 최신리뷰를 가져온다.
    suspend fun getLatestReview(movieId: String): Review?

    // 모든 리뷰를 가져온다.
    suspend fun getAllMovieReviews(movieId: String): List<Review>

    suspend fun getAllUserReviews(userId: String): List<Review>

    // 리뷰 추가
    suspend fun addReview(review: Review): Review

    // 리뷰 삭제
    suspend fun removeReview(review: Review)
}