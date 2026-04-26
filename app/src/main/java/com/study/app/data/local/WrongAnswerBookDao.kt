package com.study.app.data.local

import androidx.room.*
import com.study.app.data.local.entity.WrongAnswerBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WrongAnswerBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WrongAnswerBookEntity): Long

    @Update
    suspend fun update(entity: WrongAnswerBookEntity)

    @Delete
    suspend fun delete(entity: WrongAnswerBookEntity)

    @Query("SELECT * FROM wrong_answer_book WHERE id = :id")
    suspend fun getById(id: Long): WrongAnswerBookEntity?

    @Query("SELECT * FROM wrong_answer_book ORDER BY addedAt DESC")
    fun getAll(): Flow<List<WrongAnswerBookEntity>>

    @Query("SELECT * FROM wrong_answer_book WHERE questionId = :questionId LIMIT 1")
    suspend fun getByQuestionId(questionId: Long): WrongAnswerBookEntity?

    @Query("SELECT * FROM wrong_answer_book WHERE questionId = :questionId")
    fun getAllByQuestionId(questionId: Long): Flow<List<WrongAnswerBookEntity>>

    @Query("DELETE FROM wrong_answer_book WHERE questionId = :questionId")
    suspend fun deleteByQuestionId(questionId: Long)
}
