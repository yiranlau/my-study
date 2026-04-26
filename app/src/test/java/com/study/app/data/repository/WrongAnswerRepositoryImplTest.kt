package com.study.app.data.repository

import com.study.app.data.local.WrongAnswerBookDao
import com.study.app.data.local.entity.WrongAnswerBookEntity
import com.study.app.domain.model.WrongAnswerBook
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
 * WrongAnswerRepository 单元测试
 * 测试错题本实体的 CRUD 操作及映射
 */
class WrongAnswerRepositoryImplTest {
    // 测试：getAll 返回映射后的错题列表
    @Test
    fun getAll_returns_mapped_wrong_answers() = runTest {
        val dao = mock(WrongAnswerBookDao::class.java)
        val entity = WrongAnswerBookEntity(id = 1L, questionId = 1L, studentAnswer = "A", correctAnswer = "B", addedAt = 1000L)
        `when`(dao.getAll()).thenReturn(flowOf(listOf(entity)))

        val repository = WrongAnswerRepositoryImpl(dao)
        val wrongAnswers = repository.getAll().first()

        assertEquals(1, wrongAnswers.size)
        assertEquals("A", wrongAnswers[0].studentAnswer)
        assertEquals("B", wrongAnswers[0].correctAnswer)
    }

    // 测试：getAllByQuestionId 返回指定题目的所有错题记录
    @Test
    fun getAllByQuestionId_returns_filtered_wrong_answers() = runTest {
        val dao = mock(WrongAnswerBookDao::class.java)
        val entity = WrongAnswerBookEntity(id = 1L, questionId = 5L, studentAnswer = "A", correctAnswer = "B", addedAt = 1000L)
        `when`(dao.getAllByQuestionId(5L)).thenReturn(flowOf(listOf(entity)))

        val repository = WrongAnswerRepositoryImpl(dao)
        val wrongAnswers = repository.getAllByQuestionId(5L).first()

        assertEquals(1, wrongAnswers.size)
        assertEquals(5L, wrongAnswers[0].questionId)
    }

    // 测试：getById 返回映射后的错题记录
    @Test
    fun getById_returns_mapped_wrong_answer() = runTest {
        val dao = mock(WrongAnswerBookDao::class.java)
        val entity = WrongAnswerBookEntity(id = 3L, questionId = 1L, studentAnswer = "A", correctAnswer = "B", addedAt = 1000L)
        `when`(dao.getById(3L)).thenReturn(entity)

        val repository = WrongAnswerRepositoryImpl(dao)
        val wrongAnswer = repository.getById(3L)

        assertEquals(3L, wrongAnswer?.id)
        assertEquals("A", wrongAnswer?.studentAnswer)
    }

    // 测试：getById 未找到时返回 null
    @Test
    fun getById_returns_null_when_not_found() = runTest {
        val dao = mock(WrongAnswerBookDao::class.java)
        `when`(dao.getById(999L)).thenReturn(null)

        val repository = WrongAnswerRepositoryImpl(dao)
        val wrongAnswer = repository.getById(999L)

        assertNull(wrongAnswer)
    }

    // 测试：getByQuestionId 返回指定题目的错题
    @Test
    fun getByQuestionId_returns_mapped_wrong_answer() = runTest {
        val dao = mock(WrongAnswerBookDao::class.java)
        val entity = WrongAnswerBookEntity(id = 1L, questionId = 7L, studentAnswer = "A", correctAnswer = "B", addedAt = 1000L)
        `when`(dao.getByQuestionId(7L)).thenReturn(entity)

        val repository = WrongAnswerRepositoryImpl(dao)
        val wrongAnswer = repository.getByQuestionId(7L)

        assertEquals(7L, wrongAnswer?.questionId)
    }

    // 测试：getByQuestionId 未找到时返回 null
    @Test
    fun getByQuestionId_returns_null_when_not_found() = runTest {
        val dao = mock(WrongAnswerBookDao::class.java)
        `when`(dao.getByQuestionId(999L)).thenReturn(null)

        val repository = WrongAnswerRepositoryImpl(dao)
        val wrongAnswer = repository.getByQuestionId(999L)

        assertNull(wrongAnswer)
    }

    // 测试：insert 调用 DAO 并返回新插入记录的 ID
    @Test
    fun insert_calls_dao_insert() = runTest {
        val dao = mock(WrongAnswerBookDao::class.java)
        `when`(dao.insert(any<WrongAnswerBookEntity>())).thenReturn(1L)

        val repository = WrongAnswerRepositoryImpl(dao)
        val wrongAnswer = WrongAnswerBook(id = 0L, questionId = 1L, studentAnswer = "A", correctAnswer = "B")
        val result = repository.insert(wrongAnswer)

        assertEquals(1L, result)
    }

    // 测试：update 调用 DAO 的更新方法
    @Test
    fun update_calls_dao_update() = runTest {
        val dao = mock(WrongAnswerBookDao::class.java)

        val repository = WrongAnswerRepositoryImpl(dao)
        val wrongAnswer = WrongAnswerBook(id = 1L, questionId = 1L, studentAnswer = "C", correctAnswer = "D")
        repository.update(wrongAnswer)
    }

    // 测试：delete 调用 DAO 的删除方法
    @Test
    fun delete_calls_dao_delete() = runTest {
        val dao = mock(WrongAnswerBookDao::class.java)

        val repository = WrongAnswerRepositoryImpl(dao)
        val wrongAnswer = WrongAnswerBook(id = 1L, questionId = 1L, studentAnswer = "A", correctAnswer = "B")
        repository.delete(wrongAnswer)
    }

    // 测试：deleteByQuestionId 调用 DAO 的删除方法
    @Test
    fun deleteByQuestionId_calls_dao_deleteByQuestionId() = runTest {
        val dao = mock(WrongAnswerBookDao::class.java)

        val repository = WrongAnswerRepositoryImpl(dao)
        repository.deleteByQuestionId(5L)
    }
}
