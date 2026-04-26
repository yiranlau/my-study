package com.study.app.data.local.entity

import com.study.app.domain.model.Question
import com.study.app.domain.model.QuestionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestionEntityTest {
    @Test
    fun toDomain_maps_all_fields() {
        val timestamp = System.currentTimeMillis()
        val entity = QuestionEntity(
            id = 1L,
            subjectId = 2L,
            gradeId = 3L,
            type = QuestionType.CHOICE,
            content = "1+1=?",
            options = "[\"1\",\"2\",\"3\",\"4\"]",
            answer = "2",
            hint = "Think about it",
            createdAt = timestamp
        )

        val domain = entity.toDomain()

        assertEquals(1L, domain.id)
        assertEquals(2L, domain.subjectId)
        assertEquals(3L, domain.gradeId)
        assertEquals(QuestionType.CHOICE, domain.type)
        assertEquals("1+1=?", domain.content)
        assertEquals("[\"1\",\"2\",\"3\",\"4\"]", domain.options)
        assertEquals("2", domain.answer)
        assertEquals("Think about it", domain.hint)
        assertEquals(timestamp, domain.createdAt)
    }

    @Test
    fun fromDomain_maps_all_fields() {
        val timestamp = System.currentTimeMillis()
        val domain = Question(
            id = 1L,
            subjectId = 2L,
            gradeId = 3L,
            type = QuestionType.FILL_BLANK,
            content = "2+2=?",
            options = null,
            answer = "4",
            hint = null,
            createdAt = timestamp
        )

        val entity = QuestionEntity.fromDomain(domain)

        assertEquals(1L, entity.id)
        assertEquals(2L, entity.subjectId)
        assertEquals(3L, entity.gradeId)
        assertEquals(QuestionType.FILL_BLANK, entity.type)
        assertEquals("2+2=?", entity.content)
        assertNull(entity.options)
        assertEquals("4", entity.answer)
        assertNull(entity.hint)
        assertEquals(timestamp, entity.createdAt)
    }

    @Test
    fun fromDomain_with_default_values() {
        val domain = Question(
            type = QuestionType.CHOICE,
            content = "What is A?",
            answer = "A"
        )

        val entity = QuestionEntity.fromDomain(domain)

        assertEquals(0L, entity.id)
        assertEquals(0L, entity.subjectId)
        assertEquals(0L, entity.gradeId)
        assertEquals(QuestionType.CHOICE, entity.type)
    }
}
