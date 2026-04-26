package com.study.app.data.local

import androidx.room.*
import com.study.app.data.local.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SubjectEntity): Long

    @Update
    suspend fun update(entity: SubjectEntity)

    @Delete
    suspend fun delete(entity: SubjectEntity)

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getById(id: Long): SubjectEntity?

    @Query("SELECT * FROM subjects ORDER BY createdAt ASC")
    fun getAll(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects ORDER BY createdAt ASC")
    suspend fun getAllOnce(): List<SubjectEntity>

    @Query("SELECT * FROM subjects WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): SubjectEntity?
}
