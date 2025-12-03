package com.jozeftvrdy.game.guessorder.di

import com.jozeftvrdy.game.guessorder.repository.GameRepository
import com.jozeftvrdy.game.guessorder.repository.GameRepositoryImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val appModule = module {
    factory<GameRepository> {
        GameRepositoryImpl(
            get(),
            get(),
        )
    }

    single<AppDispatchers> {
        object : AppDispatchers {
            override val IO: CoroutineDispatcher
                get() = Dispatchers.IO
            override val Default: CoroutineDispatcher
                get() = Dispatchers.Default
            override val Main: CoroutineDispatcher
                get() = Dispatchers.Main
        }
    }
}