package com.jozeftvrdy.game.guessorder.game.create

import com.jozeftvrdy.game.guessorder.game.model.InitialGameData

sealed class ScreenState {
    data object Loading: ScreenState()
    data class Loaded(
        val savedInitialGameData: InitialGameData,
        val tilesValueRange: IntRange,
        val colorsValueRange: IntRange,
    ): ScreenState()

}

sealed class ScreenEffect {
    data class NavigateToGame(val gameData: InitialGameData): ScreenEffect()
}