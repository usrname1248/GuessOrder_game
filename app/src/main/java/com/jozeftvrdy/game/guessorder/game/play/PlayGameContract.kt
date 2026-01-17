package com.jozeftvrdy.game.guessorder.game.play

data object ScreenState
sealed class ScreenEffect {
    data object NavigateToPostGame: ScreenEffect()
    data object ShowUnexpectedError: ScreenEffect()
}
