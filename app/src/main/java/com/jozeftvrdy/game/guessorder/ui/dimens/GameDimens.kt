package com.jozeftvrdy.game.guessorder.ui.dimens

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class CreateGameDimens(
    val tileSize: Dp,
    val tileBorderWidth: Dp,
    val tileFillSize: Dp,
    val tileFillPadding: Dp = (tileSize - tileFillSize).div(2),
    val tileProgressWidth: Dp,
    val tileInnerSpacing: Dp,
    val tileOuterSpacing: Dp,
)

val createGameDimens : CreateGameDimens
    @Composable get() = createGameDefaultDimens

private val createGameDefaultDimens = CreateGameDimens(
    tileSize = 80.dp,
    tileBorderWidth = 2.dp,
    tileFillSize = 64.dp,
    tileProgressWidth = 4.dp,
    tileInnerSpacing = 16.dp,
    tileOuterSpacing = 24.dp,
)