package com.jozeftvrdy.game.guessorder.game.play

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PlayGameDimens (
    val playTileSize: Dp,
    val historyTileSize: Dp,
    val historyRowOuterPadding: PaddingValues,
    val historyResultIconSize: Dp = playTileSize.times(0.75f),
    val historyResultTextSize: TextUnit = playTileSize.value.times(0.66f).sp,
    val historyResultProgressSize: Dp = playTileSize.times(0.8f),
)

internal val playGameDimens: PlayGameDimens
    @Composable get() = playGameDefaultDimes


private val playGameDefaultDimes = PlayGameDimens(
    playTileSize = 64.dp,
    historyTileSize = 48.dp,
    historyRowOuterPadding = PaddingValues(
        vertical = 8.dp,
        horizontal = 24.dp
    ),
)