package com.jozeftvrdy.game.guessorder.repository

import android.database.sqlite.SQLiteException
import com.jozeftvrdy.game.database.dao.LastUsedInitialGameDataDao
import com.jozeftvrdy.game.guessorder.di.AppDispatchers
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import com.jozeftvrdy.game.guessorder.repository.mapper.InitGameDataMapper.fromEntity
import com.jozeftvrdy.game.guessorder.repository.mapper.InitGameDataMapper.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface GameRepository {
    fun saveInitialDataAsync(initialData: InitialGameData)
    suspend fun getInitialData(): InitialGameData
}

private const val InitialTilesCount = 4
private const val InitialColorsCount = 3

class GameRepositoryImpl(
    private val lastGameDataDao: LastUsedInitialGameDataDao,
    private val appDispatchers: AppDispatchers,
): GameRepository {

    private val repositoryScope = CoroutineScope(appDispatchers.IO + SupervisorJob())

    private val defaultInitialGameData: InitialGameData
        get() = InitialGameData(
            colorsCount = InitialColorsCount,
            tilesCount = InitialTilesCount
        )

    override fun saveInitialDataAsync(initialData: InitialGameData) {
        repositoryScope.launch {
            try {
                lastGameDataDao.set(initialData.toEntity())
            } catch (_: SQLiteException) {
            }
        }
    }

    override suspend fun getInitialData(): InitialGameData = withContext(appDispatchers.IO) {
        try {
            lastGameDataDao.get().fromEntity()
        } catch (_: SQLiteException) {
            null
        }?:defaultInitialGameData
    }
}