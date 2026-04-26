package com.study.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GradeTest {
    @Test
    fun grade_creates_with_name_and_default_values() {
        val grade = Grade(name = "一年级")
        assertEquals("一年级", grade.name)
        assertEquals(0L, grade.id)
        assertEquals(0, grade.order)
    }

    @Test
    fun grade_creates_with_order() {
        val grade = Grade(name = "二年级", order = 2)
        assertEquals("二年级", grade.name)
        assertEquals(2, grade.order)
    }

    @Test
    fun grade_creates_with_all_fields() {
        val timestamp = System.currentTimeMillis()
        val grade = Grade(id = 1L, name = "三年级", order = 3, createdAt = timestamp)
        assertEquals(1L, grade.id)
        assertEquals("三年级", grade.name)
        assertEquals(3, grade.order)
        assertEquals(timestamp, grade.createdAt)
    }
}