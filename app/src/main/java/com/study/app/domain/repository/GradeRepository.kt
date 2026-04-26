package com.study.app.domain.repository

import com.study.app.domain.model.Grade
import kotlinx.coroutines.flow.Flow

interface GradeRepository {
    fun getAll(): Flow<List<Grade>>
    suspend fun getById(id: Long): Grade?
    suspend fun getByName(name: String): Grade?
    suspend fun insert(grade: Grade): Long
    suspend fun update(grade: Grade)
    suspend fun delete(grade: Grade)
}
