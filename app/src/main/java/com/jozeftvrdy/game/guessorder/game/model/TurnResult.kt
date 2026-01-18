package com.jozeftvrdy.game.guessorder.game.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class TurnResult(
    /**
     * Correct guess of value and its tile,
     */
    val greatSuccessCount: Int,
    /**
     * Correct guess of value but on another tile
     */
    val mildSuccessCount: Int,
): Parcelable