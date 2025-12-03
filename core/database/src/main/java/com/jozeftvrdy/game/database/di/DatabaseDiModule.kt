package com.jozeftvrdy.game.database.di

import androidx.room.Room
import com.jozeftvrdy.game.database.AppDatabase
import com.jozeftvrdy.game.database.dao.LastUsedInitialGameDataDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseDiModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java, "database-name"
        ).build()
    }

    single<LastUsedInitialGameDataDao> {
        get<AppDatabase>().lastUsedInitGameDataDao()
    }
}