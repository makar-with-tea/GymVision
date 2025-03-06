package ru.hse.gymvision

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.hse.gymvision.di.appModule
import ru.hse.gymvision.di.dataModule
import ru.hse.gymvision.di.domainModule

class GymVisionApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@GymVisionApplication)
            modules(appModule,
                dataModule,
                domainModule
                )
        }
    }
}