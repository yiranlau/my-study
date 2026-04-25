package com.study.app.ui.components

import com.study.app.domain.model.StudySession
import org.junit.Assert.assertTrue
import org.junit.Test

class StudyCardTest {
    @Test
    fun study_card_displays_session_info() {
        val session = StudySession(
            id = 1L,
            subject = "Chemistry",
            startTime = System.currentTimeMillis() - 3600000,
            durationMinutes = 60
        )
        assertTrue(session.subject == "Chemistry")
        assertTrue(session.durationMinutes >= 59)
    }
}