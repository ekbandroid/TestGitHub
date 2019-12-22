package com.testgithub

import android.app.Application
import com.facebook.stetho.Stetho
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        if (BuildConfig.DEBUG) Stetho.initializeWithDefaults(this)
        startKoin {
            androidContext(this@App)
            modules(KoinModules.create())
        }
    }
}