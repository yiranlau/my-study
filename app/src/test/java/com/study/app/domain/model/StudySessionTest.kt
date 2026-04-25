package com.study.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class StudySessionTest {
    @Test
    fun study_session_has_required_fields() {
        val session = StudySession(
            id = 1L,
            subject = "Mathematics",
            startTime = 1000L,
            endTime = null,
            durationMinutes = 0
        )
        assertEquals("Mathematics", session.subject)
        assertEquals(1L, session.id)
        assertEquals(null, session.endTime)
    }
}
