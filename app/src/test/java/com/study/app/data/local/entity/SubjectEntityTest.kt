package com.study.app.data.local.entity

import com.study.app.domain.model.Subject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SubjectEntityTest {
    @Test
    fun toDomain_maps_all_fields() {
        val timestamp = System.currentTimeMillis()
        val entity = SubjectEntity(
            id = 1L,
            name = "数学",
            isDefault = true,
            createdAt = timestamp
        )

        val domain = entity.toDomain()

        assertEquals(1L, domain.id)
        assertEquals("数学", domain.name)
        assertTrue(domain.isDefault)
        assertEquals(timestamp, domain.createdAt)
    }

    @Test
    fun fromDomain_maps_all_fields() {
        val timestamp = System.currentTimeMillis()
        val domain = Subject(
            id = 1L,
            name = "语文",
            isDefault = false,
            createdAt = timestamp
        )

        val entity = SubjectEntity.fromDomain(domain)

        assertEquals(1L, entity.id)
        assertEquals("语文", entity.name)
        assertFalse(entity.isDefault)
        assertEquals(timestamp, entity.createdAt)
    }

    @Test
    fun fromDomain_with_default_values() {
        val domain = Subject(name = "英语")

        val entity = SubjectEntity.fromDomain(domain)

        assertEquals(0L, entity.id)
        assertEquals("英语", entity.name)
        assertFalse(entity.isDefault)
    }
}
