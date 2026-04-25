package com.study.app.domain.usecase

import com.study.app.domain.model.StudySession
import com.study.app.domain.repository.SessionRepository
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlinx.coroutines.test.runTest

class EndSessionUseCaseTest {
    @Test
    fun execute_sets_end_time_on_session() = runTest {
        val repository = mock(SessionRepository::class.java)
        val session = StudySession(id = 1L, subject = "Math", startTime = 1000L)
        `when`(repository.getSessionById(1L)).thenReturn(session)
        `when`(repository.updateSession(org.mockito.kotlin.any())).then {}

        val useCase = EndSessionUseCase(repository)
        val result = useCase(1L)

        assertTrue(result!!.endTime != null)
    }
}