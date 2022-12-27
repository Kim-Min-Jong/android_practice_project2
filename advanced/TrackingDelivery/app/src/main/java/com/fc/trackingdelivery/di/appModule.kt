package com.fc.trackingdelivery.di

import android.app.Activity
import com.fc.trackingdelivery.data.api.SweetTrackerApi
import com.fc.trackingdelivery.data.api.Url
import com.fc.trackingdelivery.data.db.AppDatabase
import com.fc.trackingdelivery.data.entity.TrackingInformation
import com.fc.trackingdelivery.data.entity.TrackingItem
import com.fc.trackingdelivery.data.preference.PreferenceManager
import com.fc.trackingdelivery.data.preference.SharedPreferenceManager
import com.fc.trackingdelivery.data.repository.ShippingCompanyRepository
import com.fc.trackingdelivery.data.repository.ShippingCompanyRepositoryImpl
import com.fc.trackingdelivery.data.repository.TrackingItemRepository
import com.fc.trackingdelivery.data.repository.TrackingItemRepositoryImpl
import com.fc.trackingdelivery.presentation.addtrackingitem.AddTrackingItemFragment
import com.fc.trackingdelivery.presentation.addtrackingitem.AddTrackingItemPresenter
import com.fc.trackingdelivery.presentation.addtrackingitem.AddTrackingItemsContract
import com.fc.trackingdelivery.presentation.trackinghistory.TrackingHistoryContract
import com.fc.trackingdelivery.presentation.trackinghistory.TrackingHistoryFragment
import com.fc.trackingdelivery.presentation.trackinghistory.TrackingHistoryPresenter
import com.fc.trackingdelivery.presentation.trackingitems.TrackingItemsContract
import com.fc.trackingdelivery.presentation.trackingitems.TrackingItemsFragment
import com.fc.trackingdelivery.presentation.trackingitems.TrackingItemsPresenter
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.BuildConfig
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
    single { get<AppDatabase>().trackingItemDao() }
    single { get<AppDatabase>().shippingCompanyDao() }

    // Api
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
    single<SweetTrackerApi> {
        Retrofit.Builder().baseUrl(Url.SWEET_TRACKER_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
            .create()
    }

    // Preference
    single { androidContext().getSharedPreferences("preference", Activity.MODE_PRIVATE) }
    single<PreferenceManager> { SharedPreferenceManager(get()) }

    // Repository
    single<TrackingItemRepository> { TrackingItemRepositoryImpl(get(), get(), get()) }
    single<ShippingCompanyRepository> { ShippingCompanyRepositoryImpl(get(), get(), get(), get()) }

    // Fragments
    scope<TrackingItemsFragment> {
        scoped<TrackingItemsContract.Presenter> { TrackingItemsPresenter(getSource(), get()) }
    }
    scope<AddTrackingItemFragment> {
        scoped<AddTrackingItemsContract.Presenter> {
            AddTrackingItemPresenter(getSource(), get(), get())
        }
    }
    scope<TrackingHistoryFragment> {
        scoped<TrackingHistoryContract.Presenter> { (trackingItem: TrackingItem, trackingInformation: TrackingInformation) ->
            TrackingHistoryPresenter(getSource(), get(), trackingItem, trackingInformation)
        }
    }

}