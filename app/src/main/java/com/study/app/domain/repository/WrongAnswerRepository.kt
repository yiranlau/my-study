package com.study.app.domain.repository

import com.study.app.domain.model.WrongAnswerBook
import kotlinx.coroutines.flow.Flow

interface WrongAnswerRepository {
    fun getAll(): Flow<List<WrongAnswerBook>>
    fun getAllByQuestionId(questionId: Long): Flow<List<WrongAnswerBook>>
    suspend fun getById(id: Long): WrongAnswerBook?
    suspend fun getByQuestionId(questionId: Long): WrongAnswerBook?
    suspend fun insert(wrongAnswer: WrongAnswerBook): Long
    suspend fun update(wrongAnswer: WrongAnswerBook)
    suspend fun delete(wrongAnswer: WrongAnswerBook)
    suspend fun deleteByQuestionId(questionId: Long)
}
