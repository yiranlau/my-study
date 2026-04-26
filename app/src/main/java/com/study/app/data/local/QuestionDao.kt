package com.study.app.data.local

import androidx.room.*
import com.study.app.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: QuestionEntity): Long

    @Update
    suspend fun update(entity: QuestionEntity)

    @Delete
    suspend fun delete(entity: QuestionEntity)

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getById(id: Long): QuestionEntity?

    @Query("SELECT * FROM questions ORDER BY createdAt ASC")
    fun getAll(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE subjectId = :subjectId ORDER BY createdAt ASC")
    fun getBySubjectId(subjectId: Long): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE gradeId = :gradeId ORDER BY createdAt ASC")
    fun getByGradeId(gradeId: Long): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE subjectId = :subjectId AND gradeId = :gradeId ORDER BY createdAt ASC")
    fun getBySubjectAndGrade(subjectId: Long, gradeId: Long): Flow<List<QuestionEntity>>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM questions WHERE subjectId = :subjectId")
    suspend fun getCountBySubject(subjectId: Long): Int

    @Query("SELECT * FROM questions WHERE subjectId = :subjectId AND gradeId = :gradeId ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomQuestions(subjectId: Long, gradeId: Long, count: Int): List<QuestionEntity>

    @Query("""
        SELECT COUNT(*) FROM questions
        WHERE content = :content
        AND subjectId = :subjectId
        AND gradeId = :gradeId
        AND type = :type
        AND answer = :answer
    """)
    suspend fun existsByContentAndSubjectAndGrade(
        content: String,
        subjectId: Long,
        gradeId: Long,
        type: String,
        answer: String
    ): Int
}
