package com.fc.subwayarrivalinfo.di

import android.app.Activity
import com.fc.subwayarrivalinfo.data.api.StationApi
import com.fc.subwayarrivalinfo.data.api.StationStorageApi
import com.fc.subwayarrivalinfo.data.db.AppDatabase
import com.fc.subwayarrivalinfo.data.preference.PreferenceManager
import com.fc.subwayarrivalinfo.data.preference.SharedPreferenceManager
import com.fc.subwayarrivalinfo.data.repository.StationRepository
import com.fc.subwayarrivalinfo.data.repository.StationRepositoryImpl
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

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

    // Repository
    single<StationRepository> { StationRepositoryImpl(get(), get(), get(), get()) }
}