package com.fc.gradingmovie.data.repository

import com.fc.gradingmovie.domain.model.Movie

interface MovieRepository {

    suspend fun  getAllMovies(): List<Movie>
}