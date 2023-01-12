package com.fc.gradingmovie.domain.usecase

import com.fc.gradingmovie.data.repository.ReviewRepository
import com.fc.gradingmovie.domain.model.Review

// 리뷰를 수정하는 유즈케잇,
class DeleteReviewUseCase(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(review: Review) =
        // ReviewRepository -> ReviewApi 를 통해 리뷰 삭제 메소드를 호출
        reviewRepository.removeReview(review)
}