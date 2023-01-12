package com.fc.gradingmovie.data.api

import com.fc.gradingmovie.domain.model.Movie
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class MovieFireStoreApi(
    private val fireStore: FirebaseFirestore
): MovieApi {
    override suspend fun getAllMovies(): List<Movie> =
        // firestore의 movies에서 다 가져오고
        fireStore.collection("movies")
            .get()
            // suspend 하기 떄문에 await 처리
            .await()
            // firestore에서 가져온 것을 Movie 형태로 변환
            .map { it.toObject<Movie>() }


    override suspend fun getMovies(movieIds: List<String>): List<Movie> =
        fireStore.collection("movies")
             // 필드 값(@DocumentId)이 movieIds에 속하는지 판단해서 (단일 인덱싱)
            .whereIn(FieldPath.documentId(), movieIds)
            // 가져옴옴
           .get()
            .await()
            .map { it.toObject<Movie>() }
}