package com.fc.gradingmovie.domain.model

data class MovieReviews(
    val myReview: Review?,
    val othersReview: List<Review>
)