package com.jozeftvrdy.game.guessorder.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val lightTilesColors : ImmutableList<Color> = persistentListOf(
    Color(0xFFE5484D),
    Color(0xFFF2A93B),
    Color(0xFF5C6BC0),
    Color(0xFFA3B18A),
    Color(0xFFFF7A59),
    Color(0xFF4FC3F7),
    Color(0xFF9C6ADE),
    Color(0xFFD16BA5),
    Color(0xFF26A69A),
    Color(0xFF8BC34A),
)
private val darkTilesColors : ImmutableList<Color> = persistentListOf(
    Color(0xFFB83236),
    Color(0xFFC1841D),
    Color(0xFF3949AB),
    Color(0xFF6C7A4F),
    Color(0xFFC94C32),
    Color(0xFF1E88C1),
    Color(0xFF6A3FB5),
    Color(0xFF9C3F72),
    Color(0xFF1B6F67),
    Color(0xFF5F8E2F),
)

val tileColors: ImmutableList<Color>
    @Composable get() = if (isSystemInDarkTheme()) darkTilesColors else lightTilesColors