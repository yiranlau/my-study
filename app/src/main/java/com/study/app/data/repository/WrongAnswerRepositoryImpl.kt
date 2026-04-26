package com.study.app.data.repository

import com.study.app.data.local.WrongAnswerBookDao
import com.study.app.data.local.entity.WrongAnswerBookEntity
import com.study.app.domain.model.WrongAnswerBook
import com.study.app.domain.repository.WrongAnswerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WrongAnswerRepositoryImpl @Inject constructor(
    private val wrongAnswerBookDao: WrongAnswerBookDao
) : WrongAnswerRepository {

    override fun getAll(): Flow<List<WrongAnswerBook>> {
        return wrongAnswerBookDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllByQuestionId(questionId: Long): Flow<List<WrongAnswerBook>> {
        return wrongAnswerBookDao.getAllByQuestionId(questionId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getById(id: Long): WrongAnswerBook? {
        return wrongAnswerBookDao.getById(id)?.toDomain()
    }

    override suspend fun getByQuestionId(questionId: Long): WrongAnswerBook? {
        return wrongAnswerBookDao.getByQuestionId(questionId)?.toDomain()
    }

    override suspend fun insert(wrongAnswer: WrongAnswerBook): Long {
        return wrongAnswerBookDao.insert(WrongAnswerBookEntity.fromDomain(wrongAnswer))
    }

    override suspend fun update(wrongAnswer: WrongAnswerBook) {
        wrongAnswerBookDao.update(WrongAnswerBookEntity.fromDomain(wrongAnswer))
    }

    override suspend fun delete(wrongAnswer: WrongAnswerBook) {
        wrongAnswerBookDao.delete(WrongAnswerBookEntity.fromDomain(wrongAnswer))
    }

    override suspend fun deleteByQuestionId(questionId: Long) {
        wrongAnswerBookDao.deleteByQuestionId(questionId)
    }
}
