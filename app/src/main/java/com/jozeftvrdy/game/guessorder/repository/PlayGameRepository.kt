package com.jozeftvrdy.game.guessorder.repository

import com.jozeftvrdy.game.guessorder.game.model.Result
import com.jozeftvrdy.game.guessorder.game.model.TurnResult

interface PlayGameRepository {
    fun validateTurnGuess(turnGuess: List<Long>, solution: List<Long>): Result<TurnResult>
}

