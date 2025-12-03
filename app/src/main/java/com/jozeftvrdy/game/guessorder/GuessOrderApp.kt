package com.jozeftvrdy.game.guessorder

import android.app.Application
import com.jozeftvrdy.game.database.di.databaseDiModule
import com.jozeftvrdy.game.guessorder.di.activityModule
import com.jozeftvrdy.game.guessorder.di.appModule
import com.jozeftvrdy.game.guessorder.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class GuessOrderApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@GuessOrderApp)
            modules(appModule, activityModule, viewModelModule, databaseDiModule)
        }
    }
}