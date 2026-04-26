package com.study.app.data.repository

import com.study.app.data.local.SubjectDao
import com.study.app.data.local.entity.SubjectEntity
import com.study.app.domain.model.Subject
import com.study.app.domain.repository.SubjectRepository
import com.study.app.data.local.DefaultData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao
) : SubjectRepository {

    suspend fun initializeDefaultData() {
        if (subjectDao.getAllOnce().isEmpty()) {
            DefaultData.subjects.forEach { subjectDao.insert(it) }
        }
    }

    override fun getAll(): Flow<List<Subject>> {
        return subjectDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getById(id: Long): Subject? {
        return subjectDao.getById(id)?.toDomain()
    }

    override suspend fun getByName(name: String): Subject? {
        return subjectDao.getByName(name)?.toDomain()
    }

    override suspend fun insert(subject: Subject): Long {
        return subjectDao.insert(SubjectEntity.fromDomain(subject))
    }

    override suspend fun update(subject: Subject) {
        subjectDao.update(SubjectEntity.fromDomain(subject))
    }

    override suspend fun delete(subject: Subject) {
        subjectDao.delete(SubjectEntity.fromDomain(subject))
    }
}
