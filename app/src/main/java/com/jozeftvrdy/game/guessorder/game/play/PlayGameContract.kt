package com.jozeftvrdy.game.guessorder.game.play

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import com.jozeftvrdy.game.guessorder.game.model.ItemFill
import com.jozeftvrdy.game.guessorder.game.model.TurnResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.parcelize.Parcelize


typealias TurnGuess = ImmutableList<ItemFill>
typealias HistoryItems = List<HistoryItem>

@Immutable
@Parcelize
data class HistoryItem(
    val guess: TurnGuess,
    val result: TurnResult?,
): Parcelable

@Immutable
@Parcelize
data class ScreenState(
    val history: HistoryItems,
    val initialGameData: InitialGameData,
): Parcelable

sealed class ScreenEffect {
    data object NavigateToPostGame: ScreenEffect()
    data object ShowUnexpectedError: ScreenEffect()
    data object ShowEmptyGuessError: ScreenEffect()
}
