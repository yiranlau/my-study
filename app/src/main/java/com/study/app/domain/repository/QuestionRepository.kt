package com.study.app.domain.repository

import com.study.app.domain.model.Question
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    fun getAll(): Flow<List<Question>>
    fun getBySubjectId(subjectId: Long): Flow<List<Question>>
    fun getByGradeId(gradeId: Long): Flow<List<Question>>
    fun getBySubjectAndGrade(subjectId: Long, gradeId: Long): Flow<List<Question>>
    suspend fun getById(id: Long): Question?
    suspend fun getCount(): Int
    suspend fun getCountBySubject(subjectId: Long): Int
    suspend fun insert(question: Question): Long
    suspend fun update(question: Question)
    suspend fun delete(question: Question)
    suspend fun getRandomQuestions(subjectId: Long, gradeId: Long, count: Int): List<Question>
}
