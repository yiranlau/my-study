package com.study.app.data.local.entity

import com.study.app.domain.model.ImportRecord
import org.junit.Assert.assertEquals
import org.junit.Test

class ImportRecordEntityTest {
    @Test
    fun toDomain_maps_all_fields() {
        val timestamp = System.currentTimeMillis()
        val entity = ImportRecordEntity(
            id = 1L,
            fileName = "math_questions.csv",
            totalCount = 100,
            successCount = 95,
            failCount = 5,
            createdAt = timestamp
        )

        val domain = entity.toDomain()

        assertEquals(1L, domain.id)
        assertEquals("math_questions.csv", domain.fileName)
        assertEquals(100, domain.totalCount)
        assertEquals(95, domain.successCount)
        assertEquals(5, domain.failCount)
        assertEquals(timestamp, domain.createdAt)
    }

    @Test
    fun fromDomain_maps_all_fields() {
        val timestamp = System.currentTimeMillis()
        val domain = ImportRecord(
            id = 1L,
            fileName = "english_words.csv",
            totalCount = 50,
            successCount = 48,
            failCount = 2,
            createdAt = timestamp
        )

        val entity = ImportRecordEntity.fromDomain(domain)

        assertEquals(1L, entity.id)
        assertEquals("english_words.csv", entity.fileName)
        assertEquals(50, entity.totalCount)
        assertEquals(48, entity.successCount)
        assertEquals(2, entity.failCount)
        assertEquals(timestamp, entity.createdAt)
    }
}
