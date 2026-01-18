package com.jozeftvrdy.game.guessorder.game.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class InitialGameData(
    val colorsCount: Int,
    val tilesCount: Int,
): Parcelable