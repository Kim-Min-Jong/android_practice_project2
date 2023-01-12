package com.fc.gradingmovie.domain.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId // 자동생성
    val id: String? = null
)
