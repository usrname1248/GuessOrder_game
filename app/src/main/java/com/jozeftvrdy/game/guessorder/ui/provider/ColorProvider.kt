package com.jozeftvrdy.game.guessorder.ui.provider

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.jozeftvrdy.game.guessorder.game.model.ItemFill

@Stable
interface ColorProvider {
    fun provideColorValue(fill: ItemFill, isDarkTheme: Boolean) : Color
}