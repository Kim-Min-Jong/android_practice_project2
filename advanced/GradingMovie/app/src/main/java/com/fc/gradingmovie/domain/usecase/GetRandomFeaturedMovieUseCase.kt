package com.fc.gradingmovie.domain.usecase

import com.fc.gradingmovie.data.repository.MovieRepository
import com.fc.gradingmovie.data.repository.ReviewRepository
import com.fc.gradingmovie.domain.model.FeaturedMovie

class GetRandomFeaturedMovieUseCase(
    private val movieRepository: MovieRepository,
    private val reviewRepository: ReviewRepository
) {

    suspend operator fun invoke(): FeaturedMovie? {
        // movieRepo에서 모든 영화를 가져오고
        val featuredMovies = movieRepository.getAllMovies()
            // 그 중에서 id가 null이거나 비어있는 것은 걸러냄
            .filter { it.id.isNullOrBlank().not() }
            // 마지막으로 Featured 된 것만 걸러낸다
            .filter { it.isFeatured == true }

        // 만약 Feature가 없으면면
       if (featuredMovies.isNullOrEmpty()) {
           // Featured Movie는 없다.
            return null
        }

        // 있으면 그들 중 1개를 랜덤으로 가져와서
        return featuredMovies.random()
            .let { movie ->
                // 그 영화의 id로 최신 리뷰를 가져온다.
                val latestReview = reviewRepository.getLatestReview(movie.id!!)
                // 그리고 영화와 리뷰로 평가된 영화 객체를 반환한다.
                FeaturedMovie(movie, latestReview)
            }
    }
}