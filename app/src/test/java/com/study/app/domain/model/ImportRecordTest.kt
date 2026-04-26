package com.study.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ImportRecordTest {
    @Test
    fun import_record_creates_with_all_fields() {
        val timestamp = System.currentTimeMillis()
        val record = ImportRecord(
            id = 1L,
            fileName = "questions.csv",
            totalCount = 100,
            successCount = 95,
            failCount = 5,
            createdAt = timestamp
        )
        assertEquals(1L, record.id)
        assertEquals("questions.csv", record.fileName)
        assertEquals(100, record.totalCount)
        assertEquals(95, record.successCount)
        assertEquals(5, record.failCount)
        assertEquals(timestamp, record.createdAt)
    }

    @Test
    fun import_record_has_default_values() {
        val record = ImportRecord(
            fileName = "math.csv",
            totalCount = 50,
            successCount = 50,
            failCount = 0
        )
        assertEquals(0L, record.id)
        assertEquals("math.csv", record.fileName)
        assertEquals(50, record.totalCount)
        assertEquals(50, record.successCount)
        assertEquals(0, record.failCount)
    }

    @Test
    fun import_record_calculates_fail_count() {
        val record = ImportRecord(
            fileName = "test.csv",
            totalCount = 100,
            successCount = 90,
            failCount = 10
        )
        assertEquals(100, record.totalCount)
        assertEquals(90, record.successCount)
        assertEquals(10, record.failCount)
        assertEquals(record.totalCount, record.successCount + record.failCount)
    }
}