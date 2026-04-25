package com.study.app.domain.usecase

import com.study.app.domain.model.StudySession
import com.study.app.domain.repository.SessionRepository
import javax.inject.Inject

class CreateSessionUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    suspend operator fun invoke(subject: String): Long {
        val session = StudySession(
            subject = subject,
            startTime = System.currentTimeMillis()
        )
        return repository.insertSession(session)
    }
}