package com.jozeftvrdy.game.guessorder.di

import com.jozeftvrdy.game.guessorder.game.create.CreateGameScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        CreateGameScreenViewModel(
            gameRepo = get()
        )
    }
}