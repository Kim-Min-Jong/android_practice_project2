package com.fc.gradingmovie.presentation.home

import com.fc.gradingmovie.domain.model.FeaturedMovie
import com.fc.gradingmovie.domain.model.Movie
import com.fc.gradingmovie.presentation.BasePresenter
import com.fc.gradingmovie.presentation.BaseView

interface HomeContract {
    interface View : BaseView<Presenter> {

        // 로딩 시
        fun showLoadingIndicator()

        // 로딩 가리기
        fun hideLoadingIndicator()

        // 에러 메세지 표시
        fun showErrorDescription(message: String)

        // 가져온 영화 목록 표시
        fun showMovies(
            featuredMovie: FeaturedMovie?,
            movies: List<Movie>
        )
    }

    interface Presenter : BasePresenter
}