package io.silv.jikannoto

import android.app.Application
import io.silv.jikannoto.di.appModule
import io.silv.jikannoto.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class JikanNotoApplication : Application() {

    override fun onCreate() {
        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@JikanNotoApplication)
            // Load modules
            modules(appModule, dataModule)
        }
        super.onCreate()
    }
}