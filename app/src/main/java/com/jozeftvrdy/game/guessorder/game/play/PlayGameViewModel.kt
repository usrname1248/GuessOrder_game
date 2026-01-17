package com.jozeftvrdy.game.guessorder.game.play

import com.jozeftvrdy.game.guessorder.base.BaseViewModel
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import com.jozeftvrdy.game.guessorder.repository.PlayGameRepository


class PlayGameViewModel(
    val initialGameData: InitialGameData,
    private val playRepo: PlayGameRepository,
): BaseViewModel<ScreenState, ScreenEffect>() {
    override val initialState: ScreenState
        get() = ScreenState

}