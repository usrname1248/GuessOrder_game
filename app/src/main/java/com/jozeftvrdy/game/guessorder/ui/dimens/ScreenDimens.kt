package com.jozeftvrdy.game.guessorder.ui.dimens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

data class ScreenDimens(
    val screenContentPadding: PaddingValues,
)

val screenDimens : ScreenDimens
    @Composable get() = screenDefaultDimens

private val screenDefaultDimens = ScreenDimens(
    screenContentPadding = PaddingValues(
        vertical = 8.dp,
        horizontal = 16.dp
    )
)