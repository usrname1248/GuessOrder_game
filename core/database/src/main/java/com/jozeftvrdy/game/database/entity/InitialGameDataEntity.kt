package com.jozeftvrdy.game.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val InitialGameDataTableName: String = "InitGameData"

@Entity(InitialGameDataTableName)
data class InitialGameDataEntity(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name = "color_count") val colorsCount: Int,
    @ColumnInfo(name = "tiles_count") val tilesCount: Int,
)