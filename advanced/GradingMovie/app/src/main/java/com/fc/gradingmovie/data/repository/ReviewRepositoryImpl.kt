package com.fc.gradingmovie.data.repository

import com.fc.gradingmovie.data.api.ReviewApi
import com.fc.gradingmovie.domain.model.Review

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ReviewRepositoryImpl(
    private val reviewApi: ReviewApi,
    private val dispatchers: CoroutineDispatcher
) : ReviewRepository {

    // Review Api를 통해 최신 리뷰를 가져온다.
    override suspend fun getLatestReview(movieId: String): Review? = withContext(dispatchers) {
        reviewApi.getLatestReview(movieId)
    }
}