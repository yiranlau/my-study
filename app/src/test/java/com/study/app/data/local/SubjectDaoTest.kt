package com.study.app.data.local

import com.study.app.data.local.entity.SubjectEntity
import com.study.app.domain.model.Subject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for SubjectDao and SubjectEntity.
 *
 * These tests verify:
 * 1. SubjectEntity toDomain() and fromDomain() mapping
 * 2. SubjectDao interface exists with expected methods
 */
class SubjectDaoTest {

    @Test
    fun subjectEntity_toDomain_maps_all_fields() {
        val now = System.currentTimeMillis()
        val entity = SubjectEntity(
            id = 1L,
            name = "数学",
            isDefault = true,
            createdAt = now
        )

        val domain = entity.toDomain()

        assertEquals(1L, domain.id)
        assertEquals("数学", domain.name)
        assertTrue(domain.isDefault)
        assertEquals(now, domain.createdAt)
    }

    @Test
    fun subjectEntity_fromDomain_maps_all_fields() {
        val now = System.currentTimeMillis()
        val domain = Subject(
            id = 2L,
            name = "语文",
            isDefault = false,
            createdAt = now
        )

        val entity = SubjectEntity.fromDomain(domain)

        assertEquals(2L, entity.id)
        assertEquals("语文", entity.name)
        assertFalse(entity.isDefault)
        assertEquals(now, entity.createdAt)
    }

    @Test
    fun subjectEntity_fromDomain_with_default_values() {
        val domain = Subject(name = "英语")

        val entity = SubjectEntity.fromDomain(domain)

        assertEquals(0L, entity.id)
        assertEquals("英语", entity.name)
        assertFalse(entity.isDefault)
    }

    @Test
    fun subjectEntity_toDomain_roundtrip() {
        val now = System.currentTimeMillis()
        val original = Subject(
            id = 3L,
            name = "物理",
            isDefault = true,
            createdAt = now
        )

        val entity = SubjectEntity.fromDomain(original)
        val result = entity.toDomain()

        assertEquals(original.id, result.id)
        assertEquals(original.name, result.name)
        assertEquals(original.isDefault, result.isDefault)
        assertEquals(original.createdAt, result.createdAt)
    }

    @Test
    fun subjectDao_interface_exists() {
        // Verify SubjectDao interface exists
        val daoClass = SubjectDao::class.java

        // Verify it's an interface
        assertTrue(daoClass.isInterface)
    }

    @Test
    fun subjectDao_has_required_methods() {
        // Verify SubjectDao interface has all required methods declared
        val daoClass = SubjectDao::class.java
        val methods = daoClass.declaredMethods.map { it.name }.toSet()

        // SubjectDao should have these methods (Kotlin suspend functions become methods with these names)
        assertTrue("insert method should exist", methods.contains("insert"))
        assertTrue("update method should exist", methods.contains("update"))
        assertTrue("delete method should exist", methods.contains("delete"))
        assertTrue("getById method should exist", methods.contains("getById"))
        assertTrue("getAll method should exist", methods.contains("getAll"))
        assertTrue("getByName method should exist", methods.contains("getByName"))
    }
}
