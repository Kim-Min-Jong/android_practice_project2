package com.fc.gradingmovie.domain.model

import com.google.firebase.firestore.DocumentId

data class Movie(
    // firestore에서 자동 생성
    // toObject를 사용하기(값 변환) 위해 id를 설정
    @DocumentId
    val id: String? = null,

    // Boolean 이면 이 어노테이션이 권장됨
    @field:JvmField
    val isFeatured: Boolean? = null,

    val title: String? = null,
    val actors: String? = null,
    val country: String? = null,
    val director: String? = null,
    val genre: String? = null,
    val posterUrl: String? = null,
    val rating: String? = null,
    val averageScore: Float? = null,
    val numberOfScore: Int? = null,
    val releaseYear: Int? = null,
    val runtime: Int? = null
)