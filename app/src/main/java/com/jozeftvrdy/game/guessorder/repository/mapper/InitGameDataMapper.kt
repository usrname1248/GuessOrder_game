package com.jozeftvrdy.game.guessorder.repository.mapper

import com.jozeftvrdy.game.database.entity.InitialGameDataEntity
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData

object InitGameDataMapper {
    fun InitialGameData.toEntity(): InitialGameDataEntity = InitialGameDataEntity(
        colorsCount = this.colorsCount,
        tilesCount = this.tilesCount,
    )

    fun InitialGameDataEntity?.fromEntity(): InitialGameData? {
        this?:return null

        return InitialGameData(
            colorsCount = this.colorsCount,
            tilesCount = this.tilesCount,
        )
    }
}