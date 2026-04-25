package com.study.app.data.repository

import com.study.app.data.local.SessionDao
import com.study.app.data.local.SessionEntity
import com.study.app.domain.model.StudySession
import com.study.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
) : SessionRepository {

    override fun getAllSessions(): Flow<List<StudySession>> {
        return sessionDao.getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getActiveSession(): Flow<StudySession?> {
        return sessionDao.getActiveSession().map { it?.toDomain() }
    }

    override suspend fun getSessionById(id: Long): StudySession? {
        return sessionDao.getSessionById(id)?.toDomain()
    }

    override suspend fun insertSession(session: StudySession): Long {
        return sessionDao.insertSession(session.toEntity())
    }

    override suspend fun updateSession(session: StudySession) {
        sessionDao.updateSession(session.toEntity())
    }

    override suspend fun deleteSession(session: StudySession) {
        sessionDao.deleteSession(session.toEntity())
    }

    private fun SessionEntity.toDomain(): StudySession {
        val duration = if (endTime != null) {
            ((endTime - startTime) / 60000).toInt()
        } else {
            ((System.currentTimeMillis() - startTime) / 60000).toInt()
        }
        return StudySession(
            id = id,
            subject = subject,
            startTime = startTime,
            endTime = endTime,
            durationMinutes = duration
        )
    }

    private fun StudySession.toEntity(): SessionEntity {
        return SessionEntity(
            id = id,
            subject = subject,
            startTime = startTime,
            endTime = endTime
        )
    }
}