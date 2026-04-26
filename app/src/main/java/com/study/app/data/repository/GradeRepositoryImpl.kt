package com.study.app.data.repository

import com.study.app.data.local.GradeDao
import com.study.app.data.local.entity.GradeEntity
import com.study.app.domain.model.Grade
import com.study.app.domain.repository.GradeRepository
import com.study.app.data.local.DefaultData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeRepositoryImpl @Inject constructor(
    private val gradeDao: GradeDao
) : GradeRepository {

    suspend fun initializeDefaultData() {
        if (gradeDao.getAllOnce().isEmpty()) {
            DefaultData.grades.forEach { gradeDao.insert(it) }
        }
    }

    override fun getAll(): Flow<List<Grade>> {
        return gradeDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getById(id: Long): Grade? {
        return gradeDao.getById(id)?.toDomain()
    }

    override suspend fun getByName(name: String): Grade? {
        return gradeDao.getByName(name)?.toDomain()
    }

    override suspend fun insert(grade: Grade): Long {
        return gradeDao.insert(GradeEntity.fromDomain(grade))
    }

    override suspend fun update(grade: Grade) {
        gradeDao.update(GradeEntity.fromDomain(grade))
    }

    override suspend fun delete(grade: Grade) {
        gradeDao.delete(GradeEntity.fromDomain(grade))
    }
}
