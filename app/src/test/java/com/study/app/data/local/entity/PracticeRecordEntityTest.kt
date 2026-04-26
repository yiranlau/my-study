package com.study.app.data.local.entity

import com.study.app.domain.model.PracticeRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PracticeRecordEntityTest {
    @Test
    fun toDomain_maps_all_fields() {
        val timestamp = System.currentTimeMillis()
        val entity = PracticeRecordEntity(
            id = 1L,
            subjectId = 2L,
            gradeId = 3L,
            totalQuestions = 10,
            correctCount = 8,
            durationMillis = 60000L,
            questionResults = "[{\"q1\":\"correct\"},{\"q2\":\"wrong\"}]",
            createdAt = timestamp
        )

        val domain = entity.toDomain()

        assertEquals(1L, domain.id)
        assertEquals(2L, domain.subjectId)
        assertEquals(3L, domain.gradeId)
        assertEquals(10, domain.totalQuestions)
        assertEquals(8, domain.correctCount)
        assertEquals(60000L, domain.durationMillis)
        assertEquals("[{\"q1\":\"correct\"},{\"q2\":\"wrong\"}]", domain.questionResults)
        assertEquals(timestamp, domain.createdAt)
    }

    @Test
    fun fromDomain_maps_all_fields() {
        val timestamp = System.currentTimeMillis()
        val domain = PracticeRecord(
            id = 1L,
            subjectId = 2L,
            gradeId = 3L,
            totalQuestions = 20,
            correctCount = 15,
            durationMillis = 120000L,
            questionResults = "[{\"q1\":\"correct\"}]",
            createdAt = timestamp
        )

        val entity = PracticeRecordEntity.fromDomain(domain)

        assertEquals(1L, entity.id)
        assertEquals(2L, entity.subjectId)
        assertEquals(3L, entity.gradeId)
        assertEquals(20, entity.totalQuestions)
        assertEquals(15, entity.correctCount)
        assertEquals(120000L, entity.durationMillis)
        assertEquals("[{\"q1\":\"correct\"}]", entity.questionResults)
        assertEquals(timestamp, entity.createdAt)
    }
}
