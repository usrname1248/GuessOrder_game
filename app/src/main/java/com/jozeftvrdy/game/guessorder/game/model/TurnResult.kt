package com.jozeftvrdy.game.guessorder.game.model

import androidx.compose.runtime.Immutable

@Immutable
data class TurnResult(
    /**
     * Correct guess of value and its tile,
     */
    val greatSuccessCount: Int,
    /**
     * Correct guess of value but on another tile
     */
    val mildSuccessCount: Int,
)