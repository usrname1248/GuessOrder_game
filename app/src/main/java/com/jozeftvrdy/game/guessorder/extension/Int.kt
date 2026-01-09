package com.jozeftvrdy.game.guessorder.extension

/**
 * calculates the start position of "Int" item inside bigger this item.
 */
fun Int.mid(int: Int) : Int = (this - int).div(2)