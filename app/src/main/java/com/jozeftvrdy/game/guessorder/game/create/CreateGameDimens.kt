package com.jozeftvrdy.game.guessorder.game.create

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class CreateGameDimens(
    val tileSize: Dp,
    val counterSize: Dp,
    val tileBorderWidth: Dp,
    val tileProgressWidth: Dp,
    val tileInnerSpacing: Dp,
)

internal val createGameDimens : CreateGameDimens
    @Composable get() = createGameDefaultDimens

private val createGameDefaultDimens = CreateGameDimens(
    tileSize = 80.dp,
    counterSize = 76.dp,
    tileBorderWidth = 2.dp,
    tileProgressWidth = 4.dp,
    tileInnerSpacing = 16.dp,
)