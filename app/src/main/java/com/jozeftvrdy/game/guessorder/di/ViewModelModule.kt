package com.jozeftvrdy.game.guessorder.di

import com.jozeftvrdy.game.guessorder.game.create.CreateGameScreenViewModel
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import com.jozeftvrdy.game.guessorder.game.play.PlayGameViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        CreateGameScreenViewModel(
            savedStateHandle = get(),
            gameRepo = get()
        )
    }

    viewModel { (initialGameData: InitialGameData) ->
        PlayGameViewModel(
            savedStateHandle = get(),
            initialGameData = initialGameData,
            playRepo = get()
        )
    }
}