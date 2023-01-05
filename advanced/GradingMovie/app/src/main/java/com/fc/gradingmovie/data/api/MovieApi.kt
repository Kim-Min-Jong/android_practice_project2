package com.fc.gradingmovie.data.api

import com.fc.gradingmovie.domain.model.Movie

interface MovieApi {

    suspend fun getAllMovies(): List<Movie>
}