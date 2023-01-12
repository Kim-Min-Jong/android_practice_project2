package com.fc.gradingmovie.domain.usecase

import com.fc.gradingmovie.data.repository.ReviewRepository
import com.fc.gradingmovie.data.repository.UserRepository
import com.fc.gradingmovie.domain.model.Movie
import com.fc.gradingmovie.domain.model.Review
import com.fc.gradingmovie.domain.model.User

// 리뷰를 작성하는 유즈케이스
class SubmitReviewUseCase(
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository
) {

    suspend operator fun invoke(
        movie: Movie,
        content: String,
        score: Float
    ): Review {
        // 현재 유저정보를 가져와서
        var user = userRepository.getUser()

        // 유저가 없으면 새로 만들고 그 유저를 현재 유저로 설정
        if (user == null) {
            userRepository.saveUser(User())
            user = userRepository.getUser()
        }

        // ReviewRepository -> ReviewApi 를 통해 리뷰 작성 메소드를 호출
        return reviewRepository.addReview(
            Review(
                userId = user!!.id,
                movieId = movie.id,
                content = content,
                score = score
            )
        )
    }
}