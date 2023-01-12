package com.fc.gradingmovie.di

import android.app.Activity
import com.fc.gradingmovie.data.api.*
import com.fc.gradingmovie.data.preference.PreferenceManager
import com.fc.gradingmovie.data.preference.SharedPreferenceManager
import com.fc.gradingmovie.data.repository.*
import com.fc.gradingmovie.domain.model.Movie
import com.fc.gradingmovie.domain.usecase.*
import com.fc.gradingmovie.presentation.home.HomeContract
import com.fc.gradingmovie.presentation.home.HomeFragment
import com.fc.gradingmovie.presentation.home.HomePresenter
import com.fc.gradingmovie.presentation.mypage.MyPageContract
import com.fc.gradingmovie.presentation.mypage.MyPageFragment
import com.fc.gradingmovie.presentation.mypage.MyPagePresenter
import com.fc.gradingmovie.presentation.reviews.MovieReviewsContract
import com.fc.gradingmovie.presentation.reviews.MovieReviewsFragment
import com.fc.gradingmovie.presentation.reviews.MovieReviewsPresenter
import org.koin.dsl.module
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext

val appModule = module {
    single { Dispatchers.IO }
}

val dataModule = module {
    single { Firebase.firestore }

    single<MovieApi> { MovieFireStoreApi(get()) }
    single<ReviewApi> { ReviewFireStoreApi(get()) }
    single<UserApi> { UserFirestoreApi(get()) }

    single<MovieRepository> { MovieRepositoryImpl(get(), get()) }
    single<ReviewRepository> { ReviewRepositoryImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get(), get()) }

    single { androidContext().getSharedPreferences("preference", Activity.MODE_PRIVATE) }
    single<PreferenceManager> { SharedPreferenceManager(get()) }
}

val domainModule = module {
    factory { GetRandomFeaturedMovieUseCase(get(), get()) }
    factory { GetAllMoviesUseCase(get()) }
    factory { GetAllMovieReviewsUseCase(get(),get()) }
    factory { GetMyReviewedMoviesUseCase(get(), get(), get()) }
    factory { SubmitReviewUseCase(get(), get()) }
    factory { DeleteReviewUseCase(get()) }
}

val presenterModule = module {
    scope<HomeFragment> {
        scoped<HomeContract.Presenter> { HomePresenter(getSource(), get(), get()) }
    }
    scope<MovieReviewsFragment> {
        scoped<MovieReviewsContract.Presenter> { (movie: Movie) ->
            MovieReviewsPresenter(movie, getSource(), get(), get(), get())
        }
    }
    scope<MyPageFragment> {
        scoped<MyPageContract.Presenter> { MyPagePresenter(getSource(), get()) }
    }
}