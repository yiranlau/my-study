package com.study.app.domain.usecase

import com.study.app.domain.model.StudySession
import com.study.app.domain.repository.SessionRepository
import javax.inject.Inject

class EndSessionUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    suspend operator fun invoke(sessionId: Long): StudySession? {
        val session = repository.getSessionById(sessionId) ?: return null
        val updatedSession = session.copy(
            endTime = System.currentTimeMillis()
        )
        repository.updateSession(updatedSession)
        return updatedSession
    }
}