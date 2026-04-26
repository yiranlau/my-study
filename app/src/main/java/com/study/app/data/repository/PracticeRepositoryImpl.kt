package com.study.app.data.repository

import com.study.app.data.local.PracticeRecordDao
import com.study.app.data.local.entity.PracticeRecordEntity
import com.study.app.domain.model.PracticeRecord
import com.study.app.domain.repository.PracticeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PracticeRepositoryImpl @Inject constructor(
    private val practiceRecordDao: PracticeRecordDao
) : PracticeRepository {

    override fun getAll(): Flow<List<PracticeRecord>> {
        return practiceRecordDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getBySubjectId(subjectId: Long): Flow<List<PracticeRecord>> {
        return practiceRecordDao.getBySubjectId(subjectId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getByGradeId(gradeId: Long): Flow<List<PracticeRecord>> {
        return practiceRecordDao.getByGradeId(gradeId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getBySubjectAndGrade(subjectId: Long, gradeId: Long): Flow<List<PracticeRecord>> {
        return practiceRecordDao.getBySubjectAndGrade(subjectId, gradeId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecent(limit: Int): Flow<List<PracticeRecord>> {
        return practiceRecordDao.getRecent(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getById(id: Long): PracticeRecord? {
        return practiceRecordDao.getById(id)?.toDomain()
    }

    override suspend fun insert(record: PracticeRecord): Long {
        return practiceRecordDao.insert(PracticeRecordEntity.fromDomain(record))
    }

    override suspend fun update(record: PracticeRecord) {
        practiceRecordDao.update(PracticeRecordEntity.fromDomain(record))
    }

    override suspend fun delete(record: PracticeRecord) {
        practiceRecordDao.delete(PracticeRecordEntity.fromDomain(record))
    }
}
