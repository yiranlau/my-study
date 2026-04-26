package com.study.app.data.repository

import com.study.app.data.local.ImportRecordDao
import com.study.app.data.local.entity.ImportRecordEntity
import com.study.app.domain.model.ImportRecord
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
 * ImportRepository 单元测试
 * 测试导入记录实体的 CRUD 操作及映射
 */
class ImportRepositoryImplTest {
    // 测试：getAll 返回映射后的导入记录列表
    @Test
    fun getAll_returns_mapped_records() = runTest {
        val dao = mock(ImportRecordDao::class.java)
        val entity = ImportRecordEntity(id = 1L, fileName = "test.csv", totalCount = 100, successCount = 95, failCount = 5, createdAt = 1000L)
        `when`(dao.getAll()).thenReturn(flowOf(listOf(entity)))

        val repository = ImportRepositoryImpl(dao)
        val records = repository.getAll().first()

        assertEquals(1, records.size)
        assertEquals("test.csv", records[0].fileName)
        assertEquals(100, records[0].totalCount)
        assertEquals(95, records[0].successCount)
    }

    // 测试：getRecent 返回最近的导入记录
    @Test
    fun getRecent_returns_limited_records() = runTest {
        val dao = mock(ImportRecordDao::class.java)
        val entity1 = ImportRecordEntity(id = 1L, fileName = "test1.csv", totalCount = 100, successCount = 95, failCount = 5, createdAt = 1000L)
        val entity2 = ImportRecordEntity(id = 2L, fileName = "test2.csv", totalCount = 50, successCount = 48, failCount = 2, createdAt = 1000L)
        `when`(dao.getRecent(5)).thenReturn(flowOf(listOf(entity1, entity2)))

        val repository = ImportRepositoryImpl(dao)
        val records = repository.getRecent(5).first()

        assertEquals(2, records.size)
    }

    // 测试：getById 返回映射后的导入记录
    @Test
    fun getById_returns_mapped_record() = runTest {
        val dao = mock(ImportRecordDao::class.java)
        val entity = ImportRecordEntity(id = 5L, fileName = "test.csv", totalCount = 100, successCount = 95, failCount = 5, createdAt = 1000L)
        `when`(dao.getById(5L)).thenReturn(entity)

        val repository = ImportRepositoryImpl(dao)
        val record = repository.getById(5L)

        assertEquals(5L, record?.id)
        assertEquals("test.csv", record?.fileName)
    }

    // 测试：getById 未找到时返回 null
    @Test
    fun getById_returns_null_when_not_found() = runTest {
        val dao = mock(ImportRecordDao::class.java)
        `when`(dao.getById(999L)).thenReturn(null)

        val repository = ImportRepositoryImpl(dao)
        val record = repository.getById(999L)

        assertNull(record)
    }

    // 测试：getByFileName 返回指定文件名的导入记录
    @Test
    fun getByFileName_returns_mapped_record() = runTest {
        val dao = mock(ImportRecordDao::class.java)
        val entity = ImportRecordEntity(id = 1L, fileName = "questions.csv", totalCount = 100, successCount = 95, failCount = 5, createdAt = 1000L)
        `when`(dao.getByFileName("questions.csv")).thenReturn(entity)

        val repository = ImportRepositoryImpl(dao)
        val record = repository.getByFileName("questions.csv")

        assertEquals("questions.csv", record?.fileName)
        assertEquals(100, record?.totalCount)
    }

    // 测试：getByFileName 未找到时返回 null
    @Test
    fun getByFileName_returns_null_when_not_found() = runTest {
        val dao = mock(ImportRecordDao::class.java)
        `when`(dao.getByFileName("nonexistent.csv")).thenReturn(null)

        val repository = ImportRepositoryImpl(dao)
        val record = repository.getByFileName("nonexistent.csv")

        assertNull(record)
    }

    // 测试：insert 调用 DAO 并返回新插入记录的 ID
    @Test
    fun insert_calls_dao_insert() = runTest {
        val dao = mock(ImportRecordDao::class.java)
        `when`(dao.insert(any<ImportRecordEntity>())).thenReturn(1L)

        val repository = ImportRepositoryImpl(dao)
        val record = ImportRecord(id = 0L, fileName = "new.csv", totalCount = 50, successCount = 48, failCount = 2)
        val result = repository.insert(record)

        assertEquals(1L, result)
    }

    // 测试：update 调用 DAO 的更新方法
    @Test
    fun update_calls_dao_update() = runTest {
        val dao = mock(ImportRecordDao::class.java)

        val repository = ImportRepositoryImpl(dao)
        val record = ImportRecord(id = 1L, fileName = "updated.csv", totalCount = 50, successCount = 49, failCount = 1)
        repository.update(record)
    }

    // 测试：delete 调用 DAO 的删除方法
    @Test
    fun delete_calls_dao_delete() = runTest {
        val dao = mock(ImportRecordDao::class.java)

        val repository = ImportRepositoryImpl(dao)
        val record = ImportRecord(id = 1L, fileName = "delete.csv", totalCount = 50, successCount = 48, failCount = 2)
        repository.delete(record)
    }
}
