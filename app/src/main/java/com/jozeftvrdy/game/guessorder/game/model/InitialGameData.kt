package com.jozeftvrdy.game.guessorder.game.model

import kotlinx.serialization.Serializable

@Serializable
data class InitialGameData(
    val colorsCount: Int,
    val tilesCount: Int,
)