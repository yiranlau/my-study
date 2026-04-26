package com.study.app.data.repository

import com.study.app.data.local.ImportRecordDao
import com.study.app.data.local.entity.ImportRecordEntity
import com.study.app.domain.model.ImportRecord
import com.study.app.domain.repository.ImportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportRepositoryImpl @Inject constructor(
    private val importRecordDao: ImportRecordDao
) : ImportRepository {

    override fun getAll(): Flow<List<ImportRecord>> {
        return importRecordDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecent(limit: Int): Flow<List<ImportRecord>> {
        return importRecordDao.getRecent(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getById(id: Long): ImportRecord? {
        return importRecordDao.getById(id)?.toDomain()
    }

    override suspend fun getByFileName(fileName: String): ImportRecord? {
        return importRecordDao.getByFileName(fileName)?.toDomain()
    }

    override suspend fun insert(record: ImportRecord): Long {
        return importRecordDao.insert(ImportRecordEntity.fromDomain(record))
    }

    override suspend fun update(record: ImportRecord) {
        importRecordDao.update(ImportRecordEntity.fromDomain(record))
    }

    override suspend fun delete(record: ImportRecord) {
        importRecordDao.delete(ImportRecordEntity.fromDomain(record))
    }
}
