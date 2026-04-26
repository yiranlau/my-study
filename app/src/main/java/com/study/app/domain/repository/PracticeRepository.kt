package com.study.app.domain.repository

import com.study.app.domain.model.PracticeRecord
import kotlinx.coroutines.flow.Flow

interface PracticeRepository {
    fun getAll(): Flow<List<PracticeRecord>>
    fun getBySubjectId(subjectId: Long): Flow<List<PracticeRecord>>
    fun getByGradeId(gradeId: Long): Flow<List<PracticeRecord>>
    fun getBySubjectAndGrade(subjectId: Long, gradeId: Long): Flow<List<PracticeRecord>>
    fun getRecent(limit: Int): Flow<List<PracticeRecord>>
    suspend fun getById(id: Long): PracticeRecord?
    suspend fun insert(record: PracticeRecord): Long
    suspend fun update(record: PracticeRecord)
    suspend fun delete(record: PracticeRecord)
}
