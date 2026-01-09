package com.jozeftvrdy.game.guessorder.extension

import androidx.compose.ui.unit.Dp

/**
 * calculates the start position of "dp" item inside bigger this item.
 */
fun Dp.mid(dp: Dp) : Dp = (this - dp).div(2)