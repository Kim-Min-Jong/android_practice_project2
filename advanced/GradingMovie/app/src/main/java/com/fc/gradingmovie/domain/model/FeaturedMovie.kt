package com.fc.gradingmovie.domain.model

data class FeaturedMovie(
    val movie: Movie,
    val latestReview: Review?
)