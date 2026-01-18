package com.jozeftvrdy.game.guessorder.ui.provider

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.jozeftvrdy.game.guessorder.game.model.ItemFill

@Immutable
class DefaultColorProvider : ColorProvider {
    val lightColorValues : MutableMap<ItemFill, Long> by lazy {
        val map = mutableMapOf<ItemFill, Long>()
        ItemFill.getAll().forEach { id ->
            val colorValue = when (id) {
                ItemFill.FillA -> 0xFFE5484D
                ItemFill.FillB -> 0xFFF2A93B
                ItemFill.FillC -> 0xFF5C6BC0
                ItemFill.FillD -> 0xFFA3B18A
                ItemFill.FillE -> 0xFFFF7A59
                ItemFill.FillF -> 0xFF4FC3F7
                ItemFill.FillG -> 0xFF9C6ADE
                ItemFill.FillH -> 0xFFD16BA5
            }
            map[id] = colorValue
        }
        map
    }
    val darkColorValues : MutableMap<ItemFill, Long> by lazy {
        val map = mutableMapOf<ItemFill, Long>()
        ItemFill.getAll().forEach { id ->
            val colorValue = when (id) {
                ItemFill.FillA -> 0xFFB83236
                ItemFill.FillB -> 0xFFC1841D
                ItemFill.FillC -> 0xFF3949AB
                ItemFill.FillD -> 0xFF6C7A4F
                ItemFill.FillE -> 0xFFC94C32
                ItemFill.FillF -> 0xFF1E88C1
                ItemFill.FillG -> 0xFF6A3FB5
                ItemFill.FillH -> 0xFF9C3F72
            }
            map[id] = colorValue
        }
        map
    }

    override fun provideColorValue(fill: ItemFill, isDarkTheme: Boolean): Color {
        val colorValue = if (isDarkTheme) {
            darkColorValues[fill]?:error("No color found for $fill in Dark theme")
        } else {
            lightColorValues[fill]?:error("No color found for $fill in Light theme")
        }
        return Color(colorValue)
    }
}