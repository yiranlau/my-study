package com.study.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SubjectTest {
    @Test
    fun subject_creates_with_name_and_default_values() {
        val subject = Subject(name = "数学")
        assertEquals("数学", subject.name)
        assertEquals(0L, subject.id)
        assertFalse(subject.isDefault)
    }

    @Test
    fun subject_creates_with_name_and_isDefault_flag() {
        val subject = Subject(name = "语文", isDefault = true)
        assertEquals("语文", subject.name)
        assertTrue(subject.isDefault)
    }

    @Test
    fun subject_creates_with_all_fields() {
        val timestamp = System.currentTimeMillis()
        val subject = Subject(id = 1L, name = "英语", isDefault = false, createdAt = timestamp)
        assertEquals(1L, subject.id)
        assertEquals("英语", subject.name)
        assertFalse(subject.isDefault)
        assertEquals(timestamp, subject.createdAt)
    }
}