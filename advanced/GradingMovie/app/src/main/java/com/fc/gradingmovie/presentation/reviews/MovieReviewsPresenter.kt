package com.fc.gradingmovie.presentation.reviews

import com.fc.gradingmovie.domain.model.Movie
import com.fc.gradingmovie.domain.usecase.GetAllMovieReviewsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

// 리뷰 페이지
class MovieReviewsPresenter(
    // 주입
    override val movie: Movie,
    private val view: MovieReviewsContract.View,
    private val getAllReviews: GetAllMovieReviewsUseCase
) : MovieReviewsContract.Presenter {

    override val scope: CoroutineScope = MainScope()

    override fun onViewCreated() {
        // 영화 정보 보여주기
        view.showMovieInformation(movie)
        // 리뷰 불러오기
        fetchReviews()
    }

    override fun onDestroyView() {}

    private fun fetchReviews() = scope.launch {
        // 리뷰를 보여줌
        try {
            // 리뷰 가져오기
            view.showLoadingIndicator()
            view.showReviews(getAllReviews(movie.id!!))
        } catch (exception: Exception) {
            // 에러처리
            exception.printStackTrace()
            view.showErrorDescription("에러가 발생했어요 😢")
        } finally {
            view.hideLoadingIndicator()
        }
    }
}