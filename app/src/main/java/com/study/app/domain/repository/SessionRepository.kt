package com.study.app.domain.repository

import com.study.app.domain.model.StudySession
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun getAllSessions(): Flow<List<StudySession>>
    fun getActiveSession(): Flow<StudySession?>
    suspend fun getSessionById(id: Long): StudySession?
    suspend fun insertSession(session: StudySession): Long
    suspend fun updateSession(session: StudySession)
    suspend fun deleteSession(session: StudySession)
}
