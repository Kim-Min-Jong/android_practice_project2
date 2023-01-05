package com.fc.gradingmovie.presentation.home

import com.fc.gradingmovie.domain.usecase.GetAllMoviesUseCase
import com.fc.gradingmovie.domain.usecase.GetRandomFeaturedMovieUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class HomePresenter(
    private val view: HomeContract.View,
    // usecase 주입
    private val getRandomFeaturedMovie: GetRandomFeaturedMovieUseCase,
    private val getAllMovies: GetAllMoviesUseCase
) : HomeContract.Presenter {

    override val scope: CoroutineScope = MainScope()

    // 영화 가져오기
    override fun onViewCreated() {
        fetchMovies()
    }

    override fun onDestroyView() {}

    // 영화 목록 가져오기
    private fun fetchMovies() = scope.launch {
        try {
            // 로딩 띄우고
            view.showLoadingIndicator()
            // 추천영화 및 영화 목록 가져옴
            val featuredMovie = getRandomFeaturedMovie()
            val movies = getAllMovies()
            // 가져온거 보여줌
            view.showMovies(featuredMovie, movies)
        } catch (exception: Exception) {
            // 실패 시
            exception.printStackTrace()
            view.showErrorDescription("에러가 발생했어요 😢")
        } finally {
            // 로딩 가리기
            view.hideLoadingIndicator()
        }
    }
}