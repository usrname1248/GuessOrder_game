package com.jozeftvrdy.game.guessorder.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data object ProgressIndicatorSize {
    val large: Dp = 96.dp
    val medium: Dp = 48.dp
    val small: Dp = 24.dp
}

@Composable
fun FullScreenProgressIndicator(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        ProgressIndicatorContent(
            ProgressIndicatorSize.large
        )
    }
}


@Composable
fun ProgressIndicatorContent(
    size: Dp,
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        modifier = modifier
            .size(size),
    )
}