package com.jozeftvrdy.game.guessorder.game.create

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import com.jozeftvrdy.game.guessorder.game.model.ItemFill
import com.jozeftvrdy.game.guessorder.util.IntRangeSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed class ScreenState: Parcelable {
    @Parcelize
    data object Loading: ScreenState()
    @Immutable
    @Parcelize
    data class Loaded(
        val tileCountRowData: RowData,
        val fillCountRowData: RowData,
        val fills: List<ItemFill>
    ): ScreenState() {
        @Parcelize
        data class RowData(
            val initialCount: Int,
            val maxCount: Int,
            @Serializable(with = IntRangeSerializer::class)
            val selectableRange: IntRange,
        ): Parcelable
    }

}

sealed class ScreenEffect {
    data class NavigateToGame(val gameData: InitialGameData): ScreenEffect()
}