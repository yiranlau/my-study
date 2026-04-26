package com.study.app.data.local.entity

import com.study.app.domain.model.WrongAnswerBook
import org.junit.Assert.assertEquals
import org.junit.Test

class WrongAnswerBookEntityTest {
    @Test
    fun toDomain_maps_all_fields() {
        val timestamp = System.currentTimeMillis()
        val entity = WrongAnswerBookEntity(
            id = 1L,
            questionId = 5L,
            studentAnswer = "B",
            correctAnswer = "A",
            addedAt = timestamp
        )

        val domain = entity.toDomain()

        assertEquals(1L, domain.id)
        assertEquals(5L, domain.questionId)
        assertEquals("B", domain.studentAnswer)
        assertEquals("A", domain.correctAnswer)
        assertEquals(timestamp, domain.addedAt)
    }

    @Test
    fun fromDomain_maps_all_fields() {
        val timestamp = System.currentTimeMillis()
        val domain = WrongAnswerBook(
            id = 1L,
            questionId = 10L,
            studentAnswer = "C",
            correctAnswer = "D",
            addedAt = timestamp
        )

        val entity = WrongAnswerBookEntity.fromDomain(domain)

        assertEquals(1L, entity.id)
        assertEquals(10L, entity.questionId)
        assertEquals("C", entity.studentAnswer)
        assertEquals("D", entity.correctAnswer)
        assertEquals(timestamp, entity.addedAt)
    }
}
