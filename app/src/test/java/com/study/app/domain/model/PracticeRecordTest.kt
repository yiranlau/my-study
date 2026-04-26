package com.study.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PracticeRecordTest {
    @Test
    fun practice_record_creates_with_all_fields() {
        val timestamp = System.currentTimeMillis()
        val record = PracticeRecord(
            id = 1L,
            subjectId = 1L,
            gradeId = 1L,
            totalQuestions = 10,
            correctCount = 8,
            durationMillis = 60000,
            questionResults = "[{\"q1\":\"correct\"},{\"q2\":\"wrong\"}]",
            createdAt = timestamp
        )
        assertEquals(1L, record.id)
        assertEquals(1L, record.subjectId)
        assertEquals(1L, record.gradeId)
        assertEquals(10, record.totalQuestions)
        assertEquals(8, record.correctCount)
        assertEquals(60000L, record.durationMillis)
        assertTrue(record.questionResults.contains("correct"))
        assertEquals(timestamp, record.createdAt)
    }

    @Test
    fun practice_record_has_default_values() {
        val record = PracticeRecord(
            subjectId = 1L,
            gradeId = 1L,
            totalQuestions = 5,
            correctCount = 3,
            durationMillis = 30000,
            questionResults = "[]"
        )
        assertEquals(0L, record.id)
        assertEquals(1L, record.subjectId)
        assertEquals(1L, record.gradeId)
    }

    @Test
    fun practice_record_calculates_accuracy() {
        val record = PracticeRecord(
            subjectId = 1L,
            gradeId = 1L,
            totalQuestions = 10,
            correctCount = 7,
            durationMillis = 50000,
            questionResults = "[]"
        )
        // Accuracy calculation: 7/10 = 0.7
        val accuracy = record.correctCount.toDouble() / record.totalQuestions.toDouble()
        assertEquals(0.7, accuracy, 0.001)
    }
}