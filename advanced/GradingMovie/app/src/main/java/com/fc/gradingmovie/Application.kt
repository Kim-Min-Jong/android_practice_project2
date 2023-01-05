package com.fc.gradingmovie

import android.app.Application
import com.fc.gradingmovie.di.appModule
import com.fc.gradingmovie.di.dataModule
import com.fc.gradingmovie.di.domainModule
import com.fc.gradingmovie.di.presenterModule
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(
                if (BuildConfig.DEBUG) {
                    Level.DEBUG
                } else {
                    Level.NONE
                }
            )
            androidContext(this@Application)
            modules(appModule + dataModule + domainModule + presenterModule)
        }
    }
}