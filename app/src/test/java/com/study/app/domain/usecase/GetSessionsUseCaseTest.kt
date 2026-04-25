package com.study.app.domain.usecase

import com.study.app.domain.model.StudySession
import com.study.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class GetSessionsUseCaseTest {
    @Test
    fun execute_returns_sessions_from_repository() = runTest {
        val repository = mock(SessionRepository::class.java)
        val sessions = listOf(
            StudySession(id = 1L, subject = "Math", startTime = 1000L)
        )
        `when`(repository.getAllSessions()).thenReturn(flowOf(sessions))

        val useCase = GetSessionsUseCase(repository)
        val result = useCase().first()

        assertEquals(1, result.size)
        assertEquals("Math", result[0].subject)
    }
}