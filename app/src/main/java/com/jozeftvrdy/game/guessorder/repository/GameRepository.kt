package com.jozeftvrdy.game.guessorder.repository

import com.jozeftvrdy.game.database.dao.LastUsedInitialGameDataDao
import com.jozeftvrdy.game.guessorder.di.AppDispatchers
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import com.jozeftvrdy.game.guessorder.repository.mapper.InitGameDataMapper.fromEntity
import com.jozeftvrdy.game.guessorder.repository.mapper.InitGameDataMapper.toEntity
import kotlinx.coroutines.withContext

interface GameRepository {
    suspend fun saveInitialData(initialData: InitialGameData)
    suspend fun getInitialData(): InitialGameData?
}

class GameRepositoryImpl(
    private val lastGameDataDao: LastUsedInitialGameDataDao,
    private val appDispatchers: AppDispatchers,
): GameRepository {
    override suspend fun saveInitialData(initialData: InitialGameData) = withContext(appDispatchers.IO) {
        lastGameDataDao.set(initialData.toEntity())
    }

    override suspend fun getInitialData(): InitialGameData? = withContext(appDispatchers.IO) { lastGameDataDao.get().fromEntity() }
}