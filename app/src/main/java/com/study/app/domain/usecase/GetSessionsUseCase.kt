package com.study.app.domain.usecase

import com.study.app.domain.model.StudySession
import com.study.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSessionsUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    operator fun invoke(): Flow<List<StudySession>> {
        return repository.getAllSessions()
    }
}