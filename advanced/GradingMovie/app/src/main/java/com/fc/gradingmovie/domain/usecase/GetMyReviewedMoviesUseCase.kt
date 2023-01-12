package com.fc.gradingmovie.domain.usecase

import com.fc.gradingmovie.data.repository.MovieRepository
import com.fc.gradingmovie.data.repository.ReviewRepository
import com.fc.gradingmovie.data.repository.UserRepository
import com.fc.gradingmovie.domain.model.ReviewedMovie
import com.fc.gradingmovie.domain.model.User

// 내가 리뷰한 리뷰 갖고오는 유즈케이스
class GetMyReviewedMoviesUseCase(
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository,
    private val movieRepository: MovieRepository
) {

    suspend operator fun invoke(): List<ReviewedMovie> {
        // 현재 유저 정보를 가져옴
        val user = userRepository.getUser()

        // 유저가 없으면
        if (user == null) {
            // 새 유저를 등록하고
            userRepository.saveUser(User())
            // 빈 값 반환
            return emptyList()
        }

        // 유저를 토대로 유저가 작성한 리뷰를 가져온다.
        val reviews = reviewRepository.getAllUserReviews(user.id!!)
             // 가져온 값에서 널값이거나 빈값이인 것은 필터함
            .filter { it.movieId.isNullOrBlank().not() }

        // 필터링 후 아무것도 없으면 빈값 반환
        if (reviews.isNullOrEmpty()) {
            return emptyList()
        }

        // 그게 아니면 리뷰의 영화 아이디를 통해 영화 정보를 가져옴
        return movieRepository
            .getMovies(reviews.map { it.movieId!! })
            .mapNotNull { movie ->
                // 리뷰정보의 영화와 맞는 것을
                val relatedReview = reviews.find { it.movieId == movie.id }
                // 그것을 저장
                relatedReview?.let { ReviewedMovie(movie, it) }
            }
    }
}