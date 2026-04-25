package com.study.app.ui.screens.home

import com.study.app.domain.model.StudySession
import com.study.app.domain.usecase.CreateSessionUseCase
import com.study.app.domain.usecase.GetSessionsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class HomeViewModelTest {
    @Test
    fun sessions_are_loaded_on_init() {
        val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        try {
            val getSessions = mock(GetSessionsUseCase::class.java)
            val createSession = mock(CreateSessionUseCase::class.java)
            val sessions = listOf(
                StudySession(id = 1L, subject = "Math", startTime = 1000L)
            )
            `when`(getSessions()).thenReturn(flowOf(sessions))

            val viewModel = HomeViewModel(getSessions, createSession)

            testDispatcher.scheduler.runCurrent()

            assertTrue(viewModel.sessions.isNotEmpty())
            assertEquals("Math", viewModel.sessions[0].subject)
        } finally {
            Dispatchers.resetMain()
        }
    }
}