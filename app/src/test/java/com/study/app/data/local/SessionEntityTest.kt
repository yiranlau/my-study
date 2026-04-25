package com.study.app.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class SessionEntityTest {
    @Test
    fun session_entity_fields_match() {
        val entity = SessionEntity(
            id = 1L,
            subject = "Physics",
            startTime = 1000L,
            endTime = null
        )
        assertEquals("Physics", entity.subject)
        assertEquals(1L, entity.id)
    }
}