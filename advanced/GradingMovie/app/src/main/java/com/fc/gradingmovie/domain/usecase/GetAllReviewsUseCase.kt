package com.fc.gradingmovie.domain.usecase

import com.fc.gradingmovie.data.repository.ReviewRepository
import com.fc.gradingmovie.domain.model.Review

class GetAllMovieReviewsUseCase(private val reviewRepository: ReviewRepository) {

    suspend operator fun invoke(movieId: String): List<Review> =
        reviewRepository.getAllMovieReviews(movieId)

}