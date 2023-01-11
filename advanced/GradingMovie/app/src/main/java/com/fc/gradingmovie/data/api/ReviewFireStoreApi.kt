package com.fc.gradingmovie.data.api

import com.fc.gradingmovie.domain.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class ReviewFireStoreApi (
    private val fireStore: FirebaseFirestore
) : ReviewApi {

    // 최신 리뷰를 가져온다.
    override suspend fun getLatestReview(movieId: String): Review? =
        // fireStore의 review에서 가져온다.
        fireStore.collection("reviews")
            // movieId가 movieId인 것을
            .whereEqualTo("movieId", movieId)
            // 최신 순으로
            .orderBy("createdAt", Query.Direction.DESCENDING)
            // 한개만
            .limit(1)
            .get()
            // suspend이므로 대기
            .await()
            // Review 모델로 포팅하고
            .map { it.toObject<Review>() }
            // 첫 번째 것만 가져옴 (없으면 null)
            .firstOrNull()

    // 모든 리뷰를 가져옴 (Review타입으로)
    override suspend fun getAllMovieReviews(movieId: String): List<Review> =
        fireStore.collection("reviews")
            .whereEqualTo("movieId", movieId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
            .map { it.toObject<Review>() }

    override suspend fun getAllUserReviews(userId: String): List<Review> =
        fireStore.collection("reviews")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
            .map { it.toObject<Review>() }

}