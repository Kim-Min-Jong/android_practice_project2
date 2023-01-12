package com.fc.gradingmovie.domain.usecase

import com.fc.gradingmovie.data.repository.ReviewRepository
import com.fc.gradingmovie.data.repository.UserRepository
import com.fc.gradingmovie.domain.model.MovieReviews
import com.fc.gradingmovie.domain.model.Review
import com.fc.gradingmovie.domain.model.User

class GetAllMovieReviewsUseCase(
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(movieId: String): MovieReviews {
        // 리뷰 유저 정보가저옴
        val reviews = reviewRepository.getAllMovieReviews(movieId)
        val user = userRepository.getUser()

        // 유저가 없으면 만들고 내리뷰가 없는 리뷰리스트 생성
        if (user == null) {
            userRepository.saveUser(User())

            return MovieReviews(null, reviews)
        }

        // 내가 쓴 리뷰와 남이 쓴 리뷰들의 리스트를 구분하고 객체로 만들어 반환(모든 리뷰를 보여주기 위해)
        return MovieReviews(
            reviews.find { it.userId == user.id },
            reviews.filter { it.userId != user.id }
        )
    }
}