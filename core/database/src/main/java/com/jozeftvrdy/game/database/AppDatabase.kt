package com.jozeftvrdy.game.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jozeftvrdy.game.database.dao.LastUsedInitialGameDataDao
import com.jozeftvrdy.game.database.entity.InitialGameDataEntity

@Database(entities = [InitialGameDataEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lastUsedInitGameDataDao(): LastUsedInitialGameDataDao
}