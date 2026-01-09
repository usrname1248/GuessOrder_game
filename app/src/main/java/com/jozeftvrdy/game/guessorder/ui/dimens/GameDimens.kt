package com.jozeftvrdy.game.guessorder.ui.dimens

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class CreateGameDimens(
    val minTileSize: Dp,
    val maxTileSize: Dp,
    val counterSize: Dp,
    val tileBorderWidth: Dp,
    val tileProgressWidth: Dp,
    val tileInnerSpacing: Dp,
)

val createGameDimens : CreateGameDimens
    @Composable get() = createGameDefaultDimens

private val createGameDefaultDimens = CreateGameDimens(
    minTileSize = 48.dp,
    maxTileSize = 96.dp,
    counterSize = 80.dp,
    tileBorderWidth = 2.dp,
    tileProgressWidth = 4.dp,
    tileInnerSpacing = 16.dp,
)