package com.jozeftvrdy.game.guessorder.extension

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

fun Color.mixWith(other: Color, fraction: Float = 0.5f): Color {
    val fractionWithAlpha = (fraction - this.alpha.times(0.5f) + other.alpha.times(0.5f)).coerceIn(0f, 1f)
    return lerp(start = this, stop = other, fraction = fractionWithAlpha)
}