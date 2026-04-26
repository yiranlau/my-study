package com.study.app.data.repository

import com.study.app.data.local.SubjectDao
import com.study.app.data.local.entity.SubjectEntity
import com.study.app.domain.model.Subject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class SubjectRepositoryImplTest {
    @Test
    fun getAll_returns_mapped_subjects() = runTest {
        val dao = mock(SubjectDao::class.java)
        val entity = SubjectEntity(id = 1L, name = "Math", isDefault = false, createdAt = 1000L)
        `when`(dao.getAll()).thenReturn(flowOf(listOf(entity)))

        val repository = SubjectRepositoryImpl(dao)
        val subjects = repository.getAll().first()

        assertEquals(1, subjects.size)
        assertEquals("Math", subjects[0].name)
        assertEquals(false, subjects[0].isDefault)
    }

    @Test
    fun getById_returns_mapped_subject() = runTest {
        val dao = mock(SubjectDao::class.java)
        val entity = SubjectEntity(id = 1L, name = "Math", isDefault = false, createdAt = 1000L)
        `when`(dao.getById(1L)).thenReturn(entity)

        val repository = SubjectRepositoryImpl(dao)
        val subject = repository.getById(1L)

        assertEquals("Math", subject?.name)
    }

    @Test
    fun getByName_returns_mapped_subject() = runTest {
        val dao = mock(SubjectDao::class.java)
        val entity = SubjectEntity(id = 1L, name = "Math", isDefault = false, createdAt = 1000L)
        `when`(dao.getByName("Math")).thenReturn(entity)

        val repository = SubjectRepositoryImpl(dao)
        val subject = repository.getByName("Math")

        assertEquals(1L, subject?.id)
        assertEquals("Math", subject?.name)
    }

    @Test
    fun insert_calls_dao_insert() = runTest {
        val dao = mock(SubjectDao::class.java)
        `when`(dao.insert(SubjectEntity(id = 0L, name = "Math", isDefault = false, createdAt = 1000L))).thenReturn(1L)

        val repository = SubjectRepositoryImpl(dao)
        val subject = Subject(id = 0L, name = "Math", isDefault = false, createdAt = 1000L)
        val result = repository.insert(subject)

        assertEquals(1L, result)
    }
}
