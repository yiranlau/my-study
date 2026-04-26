package com.study.app.data.local

import androidx.room.*
import com.study.app.data.local.entity.PracticeRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PracticeRecordEntity): Long

    @Update
    suspend fun update(entity: PracticeRecordEntity)

    @Delete
    suspend fun delete(entity: PracticeRecordEntity)

    @Query("SELECT * FROM practice_records WHERE id = :id")
    suspend fun getById(id: Long): PracticeRecordEntity?

    @Query("SELECT * FROM practice_records ORDER BY createdAt DESC")
    fun getAll(): Flow<List<PracticeRecordEntity>>

    @Query("SELECT * FROM practice_records WHERE subjectId = :subjectId ORDER BY createdAt DESC")
    fun getBySubjectId(subjectId: Long): Flow<List<PracticeRecordEntity>>

    @Query("SELECT * FROM practice_records WHERE gradeId = :gradeId ORDER BY createdAt DESC")
    fun getByGradeId(gradeId: Long): Flow<List<PracticeRecordEntity>>

    @Query("SELECT * FROM practice_records WHERE subjectId = :subjectId AND gradeId = :gradeId ORDER BY createdAt DESC")
    fun getBySubjectAndGrade(subjectId: Long, gradeId: Long): Flow<List<PracticeRecordEntity>>

    @Query("SELECT * FROM practice_records ORDER BY createdAt DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<PracticeRecordEntity>>
}
