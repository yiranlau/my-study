package com.study.app.domain.repository

import com.study.app.domain.model.ImportRecord
import kotlinx.coroutines.flow.Flow

interface ImportRepository {
    fun getAll(): Flow<List<ImportRecord>>
    fun getRecent(limit: Int): Flow<List<ImportRecord>>
    suspend fun getById(id: Long): ImportRecord?
    suspend fun getByFileName(fileName: String): ImportRecord?
    suspend fun insert(record: ImportRecord): Long
    suspend fun update(record: ImportRecord)
    suspend fun delete(record: ImportRecord)
}
