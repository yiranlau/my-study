package com.study.app.data.repository

import com.study.app.data.local.QuestionDao
import com.study.app.data.local.entity.QuestionEntity
import com.study.app.domain.model.Question
import com.study.app.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepositoryImpl @Inject constructor(
    private val questionDao: QuestionDao
) : QuestionRepository {

    override fun getAll(): Flow<List<Question>> {
        return questionDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getBySubjectId(subjectId: Long): Flow<List<Question>> {
        return questionDao.getBySubjectId(subjectId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getByGradeId(gradeId: Long): Flow<List<Question>> {
        return questionDao.getByGradeId(gradeId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getBySubjectAndGrade(subjectId: Long, gradeId: Long): Flow<List<Question>> {
        return questionDao.getBySubjectAndGrade(subjectId, gradeId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getById(id: Long): Question? {
        return questionDao.getById(id)?.toDomain()
    }

    override suspend fun getCount(): Int {
        return questionDao.getCount()
    }

    override suspend fun getCountBySubject(subjectId: Long): Int {
        return questionDao.getCountBySubject(subjectId)
    }

    override suspend fun insert(question: Question): Long {
        return questionDao.insert(QuestionEntity.fromDomain(question))
    }

    override suspend fun update(question: Question) {
        questionDao.update(QuestionEntity.fromDomain(question))
    }

    override suspend fun delete(question: Question) {
        questionDao.delete(QuestionEntity.fromDomain(question))
    }

    override suspend fun getRandomQuestions(subjectId: Long, gradeId: Long, count: Int): List<Question> {
        return questionDao.getRandomQuestions(subjectId, gradeId, count).map { it.toDomain() }
    }
}
