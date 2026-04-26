package com.study.app.data.repository

import com.study.app.data.local.PracticeRecordDao
import com.study.app.data.local.entity.PracticeRecordEntity
import com.study.app.domain.model.PracticeRecord
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

/**
 * PracticeRecordRepository 单元测试
 * 测试练习记录实体的 CRUD 操作及映射
 */
class PracticeRepositoryImplTest {
    // 创建测试用 Entity 的辅助方法
    private fun createEntity(
        id: Long = 1L,
        subjectId: Long = 1L,
        gradeId: Long = 1L,
        totalQuestions: Int = 10,
        correctCount: Int = 8
    ) = PracticeRecordEntity(
        id = id,
        subjectId = subjectId,
        gradeId = gradeId,
        totalQuestions = totalQuestions,
        correctCount = correctCount,
        durationMillis = 60000L,
        questionResults = "[]",
        createdAt = 1000L
    )

    // 测试：getAll 返回映射后的练习记录列表
    @Test
    fun getAll_returns_mapped_records() = runTest {
        val dao = mock(PracticeRecordDao::class.java)
        val entity = createEntity()
        `when`(dao.getAll()).thenReturn(flowOf(listOf(entity)))

        val repository = PracticeRepositoryImpl(dao)
        val records = repository.getAll().first()

        assertEquals(1, records.size)
        assertEquals(10, records[0].totalQuestions)
        assertEquals(8, records[0].correctCount)
    }

    // 测试：getBySubjectId 返回指定学科的练习记录
    @Test
    fun getBySubjectId_returns_filtered_records() = runTest {
        val dao = mock(PracticeRecordDao::class.java)
        val entity = createEntity(subjectId = 2L)
        `when`(dao.getBySubjectId(2L)).thenReturn(flowOf(listOf(entity)))

        val repository = PracticeRepositoryImpl(dao)
        val records = repository.getBySubjectId(2L).first()

        assertEquals(1, records.size)
        assertEquals(2L, records[0].subjectId)
    }

    // 测试：getByGradeId 返回指定年级的练习记录
    @Test
    fun getByGradeId_returns_filtered_records() = runTest {
        val dao = mock(PracticeRecordDao::class.java)
        val entity = createEntity(gradeId = 3L)
        `when`(dao.getByGradeId(3L)).thenReturn(flowOf(listOf(entity)))

        val repository = PracticeRepositoryImpl(dao)
        val records = repository.getByGradeId(3L).first()

        assertEquals(1, records.size)
        assertEquals(3L, records[0].gradeId)
    }

    // 测试：getBySubjectAndGrade 返回指定学科和年级的练习记录
    @Test
    fun getBySubjectAndGrade_returns_filtered_records() = runTest {
        val dao = mock(PracticeRecordDao::class.java)
        val entity = createEntity(subjectId = 1L, gradeId = 2L)
        `when`(dao.getBySubjectAndGrade(1L, 2L)).thenReturn(flowOf(listOf(entity)))

        val repository = PracticeRepositoryImpl(dao)
        val records = repository.getBySubjectAndGrade(1L, 2L).first()

        assertEquals(1, records.size)
        assertEquals(1L, records[0].subjectId)
        assertEquals(2L, records[0].gradeId)
    }

    // 测试：getRecent 返回最近的练习记录
    @Test
    fun getRecent_returns_limited_records() = runTest {
        val dao = mock(PracticeRecordDao::class.java)
        val entity1 = createEntity(id = 1L)
        val entity2 = createEntity(id = 2L)
        `when`(dao.getRecent(5)).thenReturn(flowOf(listOf(entity1, entity2)))

        val repository = PracticeRepositoryImpl(dao)
        val records = repository.getRecent(5).first()

        assertEquals(2, records.size)
    }

    // 测试：getById 返回映射后的练习记录
    @Test
    fun getById_returns_mapped_record() = runTest {
        val dao = mock(PracticeRecordDao::class.java)
        val entity = createEntity(id = 5L)
        `when`(dao.getById(5L)).thenReturn(entity)

        val repository = PracticeRepositoryImpl(dao)
        val record = repository.getById(5L)

        assertEquals(5L, record?.id)
        assertEquals(10, record?.totalQuestions)
    }

    // 测试：getById 未找到时返回 null
    @Test
    fun getById_returns_null_when_not_found() = runTest {
        val dao = mock(PracticeRecordDao::class.java)
        `when`(dao.getById(999L)).thenReturn(null)

        val repository = PracticeRepositoryImpl(dao)
        val record = repository.getById(999L)

        assertNull(record)
    }

    // 测试：insert 调用 DAO 并返回新插入记录的 ID
    @Test
    fun insert_calls_dao_insert() = runTest {
        val dao = mock(PracticeRecordDao::class.java)
        `when`(dao.insert(any<PracticeRecordEntity>())).thenReturn(1L)

        val repository = PracticeRepositoryImpl(dao)
        val record = PracticeRecord(id = 0L, subjectId = 1L, gradeId = 1L, totalQuestions = 10, correctCount = 8, durationMillis = 60000L, questionResults = "[]")
        val result = repository.insert(record)

        assertEquals(1L, result)
    }

    // 测试：update 调用 DAO 的更新方法
    @Test
    fun update_calls_dao_update() = runTest {
        val dao = mock(PracticeRecordDao::class.java)

        val repository = PracticeRepositoryImpl(dao)
        val record = PracticeRecord(id = 1L, subjectId = 1L, gradeId = 1L, totalQuestions = 10, correctCount = 9, durationMillis = 60000L, questionResults = "[]")
        repository.update(record)
    }

    // 测试：delete 调用 DAO 的删除方法
    @Test
    fun delete_calls_dao_delete() = runTest {
        val dao = mock(PracticeRecordDao::class.java)

        val repository = PracticeRepositoryImpl(dao)
        val record = PracticeRecord(id = 1L, subjectId = 1L, gradeId = 1L, totalQuestions = 10, correctCount = 8, durationMillis = 60000L, questionResults = "[]")
        repository.delete(record)
    }
}
