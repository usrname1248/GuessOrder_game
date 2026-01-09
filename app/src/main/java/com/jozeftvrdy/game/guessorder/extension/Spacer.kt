package com.jozeftvrdy.game.guessorder.extension

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Spacer(size: Int) {
    Spacer(modifier = Modifier.size(size.dp))
}

@Composable
fun Spacer(size: Dp) {
    Spacer(modifier = Modifier.size(size))
}