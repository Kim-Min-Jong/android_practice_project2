package com.fc.gradingmovie.di

import com.fc.gradingmovie.data.api.MovieApi
import com.fc.gradingmovie.data.api.MovieFireStoreApi
import com.fc.gradingmovie.data.api.ReviewApi
import com.fc.gradingmovie.data.api.ReviewFireStoreApi
import com.fc.gradingmovie.data.repository.MovieRepository
import com.fc.gradingmovie.data.repository.MovieRepositoryImpl
import com.fc.gradingmovie.data.repository.ReviewRepository
import com.fc.gradingmovie.data.repository.ReviewRepositoryImpl
import com.fc.gradingmovie.domain.usecase.GetAllMoviesUseCase
import com.fc.gradingmovie.domain.usecase.GetAllReviewsUseCase
import com.fc.gradingmovie.domain.usecase.GetRandomFeaturedMovieUseCase
import com.fc.gradingmovie.presentation.home.HomeContract
import com.fc.gradingmovie.presentation.home.HomeFragment
import com.fc.gradingmovie.presentation.home.HomePresenter
import org.koin.dsl.module
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers

val appModule = module {
    single { Dispatchers.IO }
}

val dataModule = module {
    single { Firebase.firestore }

    single<MovieApi> { MovieFireStoreApi(get()) }
    single<ReviewApi> { ReviewFireStoreApi(get()) }

    single<MovieRepository> { MovieRepositoryImpl(get(), get()) }
    single<ReviewRepository> { ReviewRepositoryImpl(get(), get()) }
}

val domainModule = module {
    factory { GetRandomFeaturedMovieUseCase(get(), get()) }
    factory { GetAllMoviesUseCase(get()) }
    factory { GetAllReviewsUseCase(get()) }
}

val presenterModule = module {
    scope<HomeFragment> {
        scoped<HomeContract.Presenter> { HomePresenter(getSource(), get(), get()) }
    }
}