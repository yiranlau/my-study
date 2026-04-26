package com.study.app.data.local.entity

import com.study.app.domain.model.Grade
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GradeEntityTest {
    @Test
    fun toDomain_maps_all_fields() {
        val timestamp = System.currentTimeMillis()
        val entity = GradeEntity(
            id = 1L,
            name = "一年级",
            order = 1,
            createdAt = timestamp
        )

        val domain = entity.toDomain()

        assertEquals(1L, domain.id)
        assertEquals("一年级", domain.name)
        assertEquals(1, domain.order)
        assertEquals(timestamp, domain.createdAt)
    }

    @Test
    fun fromDomain_maps_all_fields() {
        val timestamp = System.currentTimeMillis()
        val domain = Grade(
            id = 2L,
            name = "二年级",
            order = 2,
            createdAt = timestamp
        )

        val entity = GradeEntity.fromDomain(domain)

        assertEquals(2L, entity.id)
        assertEquals("二年级", entity.name)
        assertEquals(2, entity.order)
        assertEquals(timestamp, entity.createdAt)
    }

    @Test
    fun fromDomain_with_default_values() {
        val domain = Grade(name = "三年级")

        val entity = GradeEntity.fromDomain(domain)

        assertEquals(0L, entity.id)
        assertEquals("三年级", entity.name)
        assertEquals(0, entity.order)
    }
}
