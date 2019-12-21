package com.testgithub

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App:Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(KoinModules.create())
        }
    }

}