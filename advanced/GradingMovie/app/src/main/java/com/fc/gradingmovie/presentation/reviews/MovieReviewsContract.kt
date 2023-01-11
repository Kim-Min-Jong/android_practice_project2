package com.fc.gradingmovie.presentation.reviews

import com.fc.gradingmovie.domain.model.Movie
import com.fc.gradingmovie.domain.model.Review
import com.fc.gradingmovie.presentation.BasePresenter
import com.fc.gradingmovie.presentation.BaseView

interface MovieReviewsContract {
    interface View : BaseView<Presenter> {

        fun showLoadingIndicator()

        fun hideLoadingIndicator()

        fun showErrorDescription(message: String)

        // 영화 정보 보여주기 (바로 뿌림)
        fun showMovieInformation(movie: Movie)

        // 기능을 나눔

        // 요청 시 리뷰 보여주기
        fun showReviews(reviews: List<Review>)
    }

    interface Presenter : BasePresenter {

        val movie: Movie
    }
}