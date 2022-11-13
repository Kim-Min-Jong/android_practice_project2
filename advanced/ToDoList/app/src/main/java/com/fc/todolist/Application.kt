package com.fc.todolist

import android.app.Application
import com.fc.todolist.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class Application: Application() {

    override fun onCreate() {
        super.onCreate()
        //koin 의존성추가
        startKoin{
            androidLogger(Level.ERROR)
            androidContext(this@Application)
            modules(appModule)
        }
    }
}