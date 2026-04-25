package com.study.app.domain.usecase

import com.study.app.domain.model.StudySession
import com.study.app.domain.repository.SessionRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlinx.coroutines.test.runTest

class CreateSessionUseCaseTest {
    @Test
    fun execute_creates_session_with_current_time() = runTest {
        val repository = mock(SessionRepository::class.java)
        `when`(repository.insertSession(org.mockito.kotlin.any())).thenReturn(1L)

        val useCase = CreateSessionUseCase(repository)
        val result = useCase("Physics")

        assertEquals(1L, result)
    }
}