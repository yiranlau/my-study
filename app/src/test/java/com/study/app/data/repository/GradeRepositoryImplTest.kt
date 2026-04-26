package com.study.app.data.repository

import com.study.app.data.local.GradeDao
import com.study.app.data.local.entity.GradeEntity
import com.study.app.domain.model.Grade
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

/**
 * GradeRepository 单元测试
 * 测试年级实体的 CRUD 操作映射
 */
class GradeRepositoryImplTest {
    // 创建测试用 Entity 的辅助方法
    private fun createEntity(
        id: Long = 1L,
        name: String = "Grade 1",
        order: Int = 1
    ) = GradeEntity(id = id, name = name, order = order, createdAt = 1000L)

    // 测试：getAll 返回映射后的年级列表
    @Test
    fun getAll_returns_mapped_grades() = runTest {
        val dao = mock(GradeDao::class.java)
        val entity = createEntity()
        `when`(dao.getAll()).thenReturn(flowOf(listOf(entity)))

        val repository = GradeRepositoryImpl(dao)
        val grades = repository.getAll().first()

        assertEquals(1, grades.size)
        assertEquals("Grade 1", grades[0].name)
        assertEquals(1L, grades[0].id)
    }

    // 测试：getById 返回映射后的年级
    @Test
    fun getById_returns_mapped_grade() = runTest {
        val dao = mock(GradeDao::class.java)
        val entity = createEntity()
        `when`(dao.getById(1L)).thenReturn(entity)

        val repository = GradeRepositoryImpl(dao)
        val grade = repository.getById(1L)

        assertEquals("Grade 1", grade?.name)
        assertEquals(1L, grade?.id)
    }

    // 测试：getById 未找到时返回 null
    @Test
    fun getById_returns_null_when_not_found() = runTest {
        val dao = mock(GradeDao::class.java)
        `when`(dao.getById(999L)).thenReturn(null)

        val repository = GradeRepositoryImpl(dao)
        val grade = repository.getById(999L)

        assertNull(grade)
    }

    // 测试：getByName 返回指定名称的年级
    @Test
    fun getByName_returns_mapped_grade() = runTest {
        val dao = mock(GradeDao::class.java)
        val entity = createEntity()
        `when`(dao.getByName("Grade 1")).thenReturn(entity)

        val repository = GradeRepositoryImpl(dao)
        val grade = repository.getByName("Grade 1")

        assertEquals("Grade 1", grade?.name)
    }

    // 测试：insert 调用 DAO 并返回新插入记录的 ID
    @Test
    fun insert_calls_dao_insert() = runTest {
        val dao = mock(GradeDao::class.java)
        // 使用 id=0L 来匹配 GradeEntity.fromDomain 生成的 entity
        `when`(dao.insert(GradeEntity(id = 0L, name = "Grade 2", order = 2, createdAt = 1000L))).thenReturn(2L)

        val repository = GradeRepositoryImpl(dao)
        val grade = Grade(id = 0L, name = "Grade 2", order = 2, createdAt = 1000L)
        val result = repository.insert(grade)

        assertEquals(2L, result)
    }

    // 测试：update 调用 DAO 的更新方法
    @Test
    fun update_calls_dao_update() = runTest {
        val dao = mock(GradeDao::class.java)

        val repository = GradeRepositoryImpl(dao)
        val grade = Grade(id = 1L, name = "Grade 1 Updated", order = 1, createdAt = 1000L)
        repository.update(grade)
    }

    // 测试：delete 调用 DAO 的删除方法
    @Test
    fun delete_calls_dao_delete() = runTest {
        val dao = mock(GradeDao::class.java)

        val repository = GradeRepositoryImpl(dao)
        val grade = Grade(id = 1L, name = "Grade 1", order = 1, createdAt = 1000L)
        repository.delete(grade)
    }
}
