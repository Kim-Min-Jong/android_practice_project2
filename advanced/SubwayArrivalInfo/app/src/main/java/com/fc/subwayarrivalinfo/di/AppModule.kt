package com.fc.subwayarrivalinfo.di

import android.app.Activity
import com.fc.subwayarrivalinfo.BuildConfig
import com.fc.subwayarrivalinfo.data.api.StationApi
import com.fc.subwayarrivalinfo.data.api.StationArrivalsApi
import com.fc.subwayarrivalinfo.data.api.StationStorageApi
import com.fc.subwayarrivalinfo.data.api.Url
import com.fc.subwayarrivalinfo.data.db.AppDatabase
import com.fc.subwayarrivalinfo.data.preference.PreferenceManager
import com.fc.subwayarrivalinfo.data.preference.SharedPreferenceManager
import com.fc.subwayarrivalinfo.data.repository.StationRepository
import com.fc.subwayarrivalinfo.data.repository.StationRepositoryImpl
import com.fc.subwayarrivalinfo.presentation.stationarrivals.StationArrivalsContract
import com.fc.subwayarrivalinfo.presentation.stationarrivals.StationArrivalsFragment
import com.fc.subwayarrivalinfo.presentation.stationarrivals.StationArrivalsPresenter
import com.fc.subwayarrivalinfo.presentation.stations.StationsContract
import com.fc.subwayarrivalinfo.presentation.stations.StationsFragment
import com.fc.subwayarrivalinfo.presentation.stations.StationsPresenter
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

val appModule = module {
    single { Dispatchers.IO }

    // Database
    single { AppDatabase.build(androidApplication()) }
    single { get<AppDatabase>().stationDao() }

    // Preference
    single { androidContext().getSharedPreferences("preference", Activity.MODE_PRIVATE) }
    single<PreferenceManager> { SharedPreferenceManager(get()) }

    // Api
    single<StationApi> { StationStorageApi(Firebase.storage) }

    single {
        OkHttpClient()
            .newBuilder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()
    }
    single<StationArrivalsApi> {
        Retrofit.Builder().baseUrl(Url.SEOUL_DATA_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
            .create()
    }
    // Repository
    single<StationRepository> { StationRepositoryImpl(get(), get(), get(), get(), get()) }

    // Presentation
    // scope - 스코프 내에서 정의된 의존성은 이 내부(여기서는 stations fragment)에서만 사용, 공유할 수 있다
    scope<StationsFragment> {
        scoped<StationsContract.Presenter> { StationsPresenter(getSource(), get()) }
    }
    scope<StationArrivalsFragment> {
        scoped<StationArrivalsContract.Presenter> { StationArrivalsPresenter(getSource(), get(), get()) }
    }

}