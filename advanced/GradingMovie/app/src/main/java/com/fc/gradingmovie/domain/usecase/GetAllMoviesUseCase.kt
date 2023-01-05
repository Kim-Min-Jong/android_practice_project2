package com.fc.gradingmovie.domain.usecase

import com.fc.gradingmovie.data.repository.MovieRepository
import com.fc.gradingmovie.domain.model.Movie

class GetAllMoviesUseCase(
    private val movieRepository: MovieRepository
) {
    // fireStore에서 목록을 다 가져옴
    suspend operator fun invoke() : List<Movie> = movieRepository.getAllMovies()
}