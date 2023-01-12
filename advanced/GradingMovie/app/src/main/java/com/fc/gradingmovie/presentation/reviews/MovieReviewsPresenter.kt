package com.fc.gradingmovie.presentation.reviews

import com.fc.gradingmovie.domain.model.Movie
import com.fc.gradingmovie.domain.model.MovieReviews
import com.fc.gradingmovie.domain.model.Review
import com.fc.gradingmovie.domain.usecase.DeleteReviewUseCase
import com.fc.gradingmovie.domain.usecase.GetAllMovieReviewsUseCase
import com.fc.gradingmovie.domain.usecase.SubmitReviewUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

// 리뷰 페이지
class MovieReviewsPresenter(
    // 주입
    override val movie: Movie,
    private val view: MovieReviewsContract.View,
    private val getAllReviews: GetAllMovieReviewsUseCase,
    private val submitReview: SubmitReviewUseCase,
    private val deleteReview: DeleteReviewUseCase
) : MovieReviewsContract.Presenter {

    override val scope: CoroutineScope = MainScope()
    private var movieReviews: MovieReviews = MovieReviews(null, emptyList())

    override fun onViewCreated() {
        // 영화 정보 보여주기
        view.showMovieInformation(movie)
        // 리뷰 불러오기
        fetchReviews()
    }

    override fun onDestroyView() {}

    // 리뷰 추가 요청
    override fun requestAddReview(content: String, score: Float) {
        scope.launch {
            try {
                view.showLoadingIndicator()
                // 리뷰 유즈케이스 실행 하고
                val submittedReview = submitReview(movie, content, score)
                // 추가된 리뷰를 보여줌
                view.showReviews(movieReviews.copy(myReview = submittedReview))
            } catch (exception: Exception) {
                exception.printStackTrace()
                view.showErrorToast("리뷰 등록을 실패했어요 😢")
            } finally {
                view.hideLoadingIndicator()
            }
        }
    }

    override fun requestRemoveReview(review: Review) {
        scope.launch {
            try {
                view.showLoadingIndicator()
                // 삭제 유즈케이스를 실행하고
                deleteReview(review)
                // 내 리뷰가 null(없어진) 리뷰들을 보여줌
                view.showReviews(movieReviews.copy(myReview = null))
            } catch (exception: Exception) {
                exception.printStackTrace()
                view.showErrorToast("리뷰 삭제를 실패했어요 😢")
            } finally {
                view.hideLoadingIndicator()
            }
        }
    }
    private fun fetchReviews() = scope.launch {
        // 리뷰를 보여줌
        try {
            // 리뷰 가져오기
            view.showLoadingIndicator()
            movieReviews = getAllReviews(movie.id!!)
            view.showReviews(movieReviews)
        } catch (exception: Exception) {
            // 에러처리
            exception.printStackTrace()
            view.showErrorDescription("에러가 발생했어요 😢")
        } finally {
            view.hideLoadingIndicator()
        }
    }
}