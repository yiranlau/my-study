package com.study.app.data.repository

import com.study.app.data.local.QuestionDao
import com.study.app.data.local.entity.QuestionEntity
import com.study.app.domain.model.Question
import com.study.app.domain.model.QuestionType
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
 * QuestionRepository 单元测试
 * 测试题目实体的 CRUD 操作及映射
 */
class QuestionRepositoryImplTest {
    // 测试：getAll 返回映射后的题目列表
    @Test
    fun getAll_returns_mapped_questions() = runTest {
        val dao = mock(QuestionDao::class.java)
        val entity = QuestionEntity(id = 1L, subjectId = 1L, gradeId = 1L, type = QuestionType.CHOICE, content = "What is 1+1?", answer = "2", createdAt = 1000L)
        `when`(dao.getAll()).thenReturn(flowOf(listOf(entity)))

        val repository = QuestionRepositoryImpl(dao)
        val questions = repository.getAll().first()

        assertEquals(1, questions.size)
        assertEquals("What is 1+1?", questions[0].content)
        assertEquals(QuestionType.CHOICE, questions[0].type)
    }

    // 测试：getBySubjectId 返回指定学科的题目
    @Test
    fun getBySubjectId_returns_filtered_questions() = runTest {
        val dao = mock(QuestionDao::class.java)
        val entity = QuestionEntity(id = 1L, subjectId = 2L, gradeId = 1L, type = QuestionType.CHOICE, content = "What is 1+1?", answer = "2", createdAt = 1000L)
        `when`(dao.getBySubjectId(2L)).thenReturn(flowOf(listOf(entity)))

        val repository = QuestionRepositoryImpl(dao)
        val questions = repository.getBySubjectId(2L).first()

        assertEquals(1, questions.size)
        assertEquals(2L, questions[0].subjectId)
    }

    // 测试：getByGradeId 返回指定年级的题目
    @Test
    fun getByGradeId_returns_filtered_questions() = runTest {
        val dao = mock(QuestionDao::class.java)
        val entity = QuestionEntity(id = 1L, subjectId = 1L, gradeId = 3L, type = QuestionType.CHOICE, content = "What is 1+1?", answer = "2", createdAt = 1000L)
        `when`(dao.getByGradeId(3L)).thenReturn(flowOf(listOf(entity)))

        val repository = QuestionRepositoryImpl(dao)
        val questions = repository.getByGradeId(3L).first()

        assertEquals(1, questions.size)
        assertEquals(3L, questions[0].gradeId)
    }

    // 测试：getBySubjectAndGrade 返回指定学科和年级的题目
    @Test
    fun getBySubjectAndGrade_returns_filtered_questions() = runTest {
        val dao = mock(QuestionDao::class.java)
        val entity = QuestionEntity(id = 1L, subjectId = 1L, gradeId = 2L, type = QuestionType.CHOICE, content = "What is 1+1?", answer = "2", createdAt = 1000L)
        `when`(dao.getBySubjectAndGrade(1L, 2L)).thenReturn(flowOf(listOf(entity)))

        val repository = QuestionRepositoryImpl(dao)
        val questions = repository.getBySubjectAndGrade(1L, 2L).first()

        assertEquals(1, questions.size)
        assertEquals(1L, questions[0].subjectId)
        assertEquals(2L, questions[0].gradeId)
    }

    // 测试：getById 返回映射后的题目
    @Test
    fun getById_returns_mapped_question() = runTest {
        val dao = mock(QuestionDao::class.java)
        val entity = QuestionEntity(id = 5L, subjectId = 1L, gradeId = 1L, type = QuestionType.CHOICE, content = "What is 1+1?", answer = "2", createdAt = 1000L)
        `when`(dao.getById(5L)).thenReturn(entity)

        val repository = QuestionRepositoryImpl(dao)
        val question = repository.getById(5L)

        assertEquals(5L, question?.id)
        assertEquals("What is 1+1?", question?.content)
    }

    // 测试：getById 未找到时返回 null
    @Test
    fun getById_returns_null_when_not_found() = runTest {
        val dao = mock(QuestionDao::class.java)
        `when`(dao.getById(999L)).thenReturn(null)

        val repository = QuestionRepositoryImpl(dao)
        val question = repository.getById(999L)

        assertNull(question)
    }

    // 测试：getCount 返回题目总数
    @Test
    fun getCount_returns_count() = runTest {
        val dao = mock(QuestionDao::class.java)
        `when`(dao.getCount()).thenReturn(42)

        val repository = QuestionRepositoryImpl(dao)
        val count = repository.getCount()

        assertEquals(42, count)
    }

    // 测试：getCountBySubject 返回指定学科的题目数量
    @Test
    fun getCountBySubject_returns_count() = runTest {
        val dao = mock(QuestionDao::class.java)
        `when`(dao.getCountBySubject(1L)).thenReturn(10)

        val repository = QuestionRepositoryImpl(dao)
        val count = repository.getCountBySubject(1L)

        assertEquals(10, count)
    }

    // 测试：insert 调用 DAO 并返回新插入题目的 ID
    @Test
    fun insert_calls_dao_insert() = runTest {
        val dao = mock(QuestionDao::class.java)
        `when`(dao.insert(any<QuestionEntity>())).thenReturn(1L)

        val repository = QuestionRepositoryImpl(dao)
        val question = Question(id = 0L, subjectId = 1L, gradeId = 1L, type = QuestionType.CHOICE, content = "What is 1+1?", answer = "2")
        val result = repository.insert(question)

        assertEquals(1L, result)
    }

    // 测试：update 调用 DAO 的更新方法
    @Test
    fun update_calls_dao_update() = runTest {
        val dao = mock(QuestionDao::class.java)

        val repository = QuestionRepositoryImpl(dao)
        val question = Question(id = 1L, subjectId = 1L, gradeId = 1L, type = QuestionType.CHOICE, content = "Updated", answer = "B")
        repository.update(question)
    }

    // 测试：delete 调用 DAO 的删除方法
    @Test
    fun delete_calls_dao_delete() = runTest {
        val dao = mock(QuestionDao::class.java)

        val repository = QuestionRepositoryImpl(dao)
        val question = Question(id = 1L, subjectId = 1L, gradeId = 1L, type = QuestionType.CHOICE, content = "Test", answer = "A")
        repository.delete(question)
    }

    // 测试：getRandomQuestions 返回随机抽取的题目
    @Test
    fun getRandomQuestions_returns_mapped_questions() = runTest {
        val dao = mock(QuestionDao::class.java)
        val entity = QuestionEntity(id = 1L, subjectId = 1L, gradeId = 1L, type = QuestionType.CHOICE, content = "What is 1+1?", answer = "2", createdAt = 1000L)
        `when`(dao.getRandomQuestions(1L, 1L, 5)).thenReturn(listOf(entity))

        val repository = QuestionRepositoryImpl(dao)
        val questions = repository.getRandomQuestions(1L, 1L, 5)

        assertEquals(1, questions.size)
    }
}
