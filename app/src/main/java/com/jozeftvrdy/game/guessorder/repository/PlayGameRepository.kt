package com.jozeftvrdy.game.guessorder.repository

import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import com.jozeftvrdy.game.guessorder.game.model.ItemFill
import com.jozeftvrdy.game.guessorder.game.model.Result
import com.jozeftvrdy.game.guessorder.game.model.TurnResult

interface PlayGameRepository {
    fun validateTurnGuess(turnGuess: List<ItemFill>, solution: List<ItemFill>): Result<TurnResult>

    fun generateSolution(turnGuess: List<ItemFill>, gameData: InitialGameData): List<ItemFill>
}

