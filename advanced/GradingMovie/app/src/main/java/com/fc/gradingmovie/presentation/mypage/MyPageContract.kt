package com.fc.gradingmovie.presentation.mypage

import com.fc.gradingmovie.domain.model.ReviewedMovie
import com.fc.gradingmovie.presentation.BasePresenter
import com.fc.gradingmovie.presentation.BaseView

interface MyPageContract {
    interface View : BaseView<Presenter> {

        fun showLoadingIndicator()

        fun hideLoadingIndicator()

        fun showNoDataDescription(message: String)

        fun showErrorDescription(message: String)

        fun showReviewedMovies(reviewedMovies: List<ReviewedMovie>)
    }

    interface Presenter : BasePresenter
}