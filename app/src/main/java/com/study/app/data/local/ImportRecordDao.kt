package com.study.app.data.local

import androidx.room.*
import com.study.app.data.local.entity.ImportRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImportRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ImportRecordEntity): Long

    @Update
    suspend fun update(entity: ImportRecordEntity)

    @Delete
    suspend fun delete(entity: ImportRecordEntity)

    @Query("SELECT * FROM import_records WHERE id = :id")
    suspend fun getById(id: Long): ImportRecordEntity?

    @Query("SELECT * FROM import_records ORDER BY createdAt DESC")
    fun getAll(): Flow<List<ImportRecordEntity>>

    @Query("SELECT * FROM import_records ORDER BY createdAt DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<ImportRecordEntity>>

    @Query("SELECT * FROM import_records WHERE fileName = :fileName LIMIT 1")
    suspend fun getByFileName(fileName: String): ImportRecordEntity?
}
