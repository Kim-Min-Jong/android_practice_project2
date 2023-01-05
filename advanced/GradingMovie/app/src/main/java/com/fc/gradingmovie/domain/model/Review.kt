package com.fc.gradingmovie.domain.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Review(
    @DocumentId
    val id: String? = null,

    // 여기에 값이 들어올 시 자동으로 시간이 지정됨
    @ServerTimestamp
    val createdAt: Date? = null,

    val userId: String? = null,
    val movieId: String? = null,
    val content: String? = null,
    val score: Float? = null
)
