package com.study.app.domain.repository

import com.study.app.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {
    fun getAll(): Flow<List<Subject>>
    suspend fun getById(id: Long): Subject?
    suspend fun getByName(name: String): Subject?
    suspend fun insert(subject: Subject): Long
    suspend fun update(subject: Subject)
    suspend fun delete(subject: Subject)
}
