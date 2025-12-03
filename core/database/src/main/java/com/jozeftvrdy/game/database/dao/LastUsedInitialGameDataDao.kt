package com.jozeftvrdy.game.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.jozeftvrdy.game.database.entity.InitialGameDataEntity
import com.jozeftvrdy.game.database.entity.InitialGameDataTableName

@Dao
interface LastUsedInitialGameDataDao {
    @Query("SELECT * FROM $InitialGameDataTableName LIMIT 1")
    fun get(): InitialGameDataEntity?

    @Transaction
    fun set(entity: InitialGameDataEntity) {
        clear()
        insert(entity)
    }

    @Insert
    fun insert(entity: InitialGameDataEntity)

    @Query("DELETE FROM $InitialGameDataTableName")
    fun clear()
}