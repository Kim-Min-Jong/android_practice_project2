package com.fc.gradingmovie.data.api

import com.fc.gradingmovie.domain.model.Movie
import com.fc.gradingmovie.domain.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
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
             // 컴포지트 인덱싱 (id + 날짜)
            .whereEqualTo("movieId", movieId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
            .map { it.toObject<Review>() }

    override suspend fun getAllUserReviews(userId: String): List<Review> =
        fireStore.collection("reviews")
            // 컴포지트 인덱싱 (id + 날짜)
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
            .map { it.toObject<Review>() }


    override suspend fun addReview(review: Review): Review {
        // 먼저 firestore collectio에서 정보 문서를 가져옴
        val newReviewReference = fireStore.collection("reviews").document()
        val movieReference = fireStore.collection("movies").document(review.movieId!!)

        /*
          firstore transaction (DB transaction 특성을 갖고 있음)
          트랜잭션 특성에 따라 (실패면 전과정 롤백, 성공이면 모두 성공) 필요 시 사용한다.
         */
        fireStore.runTransaction { transaction ->
            // movies collection에서 객체를 가져온다.
            // get() transaction에 앞에 있지않으면 에러 발생
            val movie = transaction.get(movieReference).toObject<Movie>()!!

            // 저장된 점수
            val oldAverageScore = movie.averageScore ?: 0f
            val oldNumberOfScore = movie.numberOfScore ?: 0
            val oldTotalScore = oldAverageScore * oldNumberOfScore

            // 새 점수 (이전 총점에서 리뷰 쓴 평점을 더해 평균)
            val newNumberOfScore = oldNumberOfScore + 1
            val newAverageScore = (oldTotalScore + (review.score ?: 0f)) / newNumberOfScore

            /*
                firestore 트랜잭션에서는 데이터 경합(동시 접근)시 실패나 오류를 반환해서
                데이터무결성을 보장한다.
             */

            // set을 통해 업데이트 한다.
            transaction.set(
                movieReference,
                movie.copy(
                    numberOfScore = newNumberOfScore,
                    averageScore = newAverageScore
                )
            )

            // set을 통해 업데이트 한다.
            transaction.set(
                newReviewReference,
                review,
                // 바뀐 부분을 merge 한다. (default는 덮어쓰기)
                SetOptions.merge()
            )
        }.await()

        return newReviewReference.get().await().toObject<Review>()!!
    }

    override suspend fun removeReview(review: Review) {
        // 참조값 가져옴
        val reviewReference = fireStore.collection("reviews").document(review.id!!)
        val movieReference = fireStore.collection("movies").document(review.movieId!!)

        // 트랜잭션 실행
        fireStore.runTransaction { transaction ->
            // 정보 가져옴
            val movie = transaction
                .get(movieReference)
                .toObject<Movie>()!!

            // 예전 값을 가져오고
            val oldAverageScore = movie.averageScore ?: 0f
            val oldNumberOfScore = movie.numberOfScore ?: 0
            val oldTotalScore = oldAverageScore * oldNumberOfScore

            // 삭제이므로 평점 계산을 새로하기위한 작업

            // 최소값이 0일때 삭제하진 않으니 0이하로 내려가지않게함
            val newNumberOfScore = (oldNumberOfScore - 1).coerceAtLeast(0)
            // 음수면 0, 아니면 새로운 점수 계산(이전 총점에서 삭제하는 평점 빼서 평균)
            val newAverageScore = if (newNumberOfScore > 0) {
                (oldTotalScore - (review.score ?: 0f)) / newNumberOfScore
            } else {
                0f
            }

            // 다시 계산한 평점 기존 평점에 덮어쓰기
            transaction.set(
                movieReference,
                movie.copy(
                    numberOfScore = newNumberOfScore,
                    averageScore = newAverageScore
                )
            )

            // 리뷰 객체 삭제 트랜잭션
            transaction.delete(reviewReference)
        }.await()
    }
}