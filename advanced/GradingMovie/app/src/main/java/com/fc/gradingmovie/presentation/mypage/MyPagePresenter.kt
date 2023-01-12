package com.fc.gradingmovie.presentation.mypage

import com.fc.gradingmovie.domain.usecase.GetMyReviewedMoviesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.lang.Exception

class MyPagePresenter(
    private val view: MyPageContract.View,
    private val getMyReviewedMovies: GetMyReviewedMoviesUseCase
) : MyPageContract.Presenter {

    override val scope: CoroutineScope = MainScope()

    override fun onViewCreated() {
        fetchReviewedMovies()
    }

    override fun onDestroyView() {}

    // 리뷰있는 영화 목록을 가져옴
    private fun fetchReviewedMovies() = scope.launch {
        try {
            view.showLoadingIndicator()

            // 유즈케이스 실행(리뷰영화목록 가져오기)
            val reviewedMovies = getMyReviewedMovies()
            // 가져온 데이터 상황에 떄라 안내창이나 영화 목록 보여주기
            if (reviewedMovies.isNullOrEmpty()) {
                view.showNoDataDescription("아직 리뷰한 영화가 없어요.\n홈 탭을 눌러 영화를 리뷰해보세요 🙌")
            } else {
                view.showReviewedMovies(reviewedMovies)
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            view.showErrorDescription("에러가 발생했어요 😢")
        } finally {
            view.hideLoadingIndicator()
        }
    }
}