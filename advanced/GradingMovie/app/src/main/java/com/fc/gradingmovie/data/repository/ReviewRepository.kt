package com.fc.gradingmovie.data.repository

import com.fc.gradingmovie.domain.model.Review

interface ReviewRepository {
    // 영화 id로 그 영화의 최신 리뷰를 가져온다.
    suspend fun getLatestReview(movieId: String): Review?
}