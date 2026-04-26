package com.study.app.data.local

import androidx.room.*
import com.study.app.data.local.entity.GradeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GradeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: GradeEntity): Long

    @Update
    suspend fun update(entity: GradeEntity)

    @Delete
    suspend fun delete(entity: GradeEntity)

    @Query("SELECT * FROM grades WHERE id = :id")
    suspend fun getById(id: Long): GradeEntity?

    @Query("SELECT * FROM grades ORDER BY `order` ASC")
    fun getAll(): Flow<List<GradeEntity>>

    @Query("SELECT * FROM grades WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): GradeEntity?
}
