package com.study.app.data.repository

import com.study.app.data.local.SessionDao
import com.study.app.data.local.SessionEntity
import com.study.app.domain.model.StudySession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class SessionRepositoryImplTest {
    @Test
    fun getAllSessions_returns_mapped_sessions() = runTest {
        val dao = mock(SessionDao::class.java)
        val entity = SessionEntity(id = 1L, subject = "Math", startTime = 1000L, endTime = null)
        `when`(dao.getAllSessions()).thenReturn(flowOf(listOf(entity)))

        val repository = SessionRepositoryImpl(dao)
        val sessions = repository.getAllSessions().first()

        assertEquals(1, sessions.size)
        assertEquals("Math", sessions[0].subject)
    }
}