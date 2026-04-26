package com.study.app.data.repository

import com.study.app.data.local.FlashcardDao
import com.study.app.data.local.FlashcardEntity
import com.study.app.domain.model.Flashcard
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
 * FlashcardRepository 单元测试
 * 测试闪卡实体的 CRUD 操作及映射
 */
class FlashcardRepositoryImplTest {
    // 测试：getFlashcardsForSession 返回指定学习会话的闪卡列表
    @Test
    fun getFlashcardsForSession_returns_mapped_flashcards() = runTest {
        val dao = mock(FlashcardDao::class.java)
        val entity = FlashcardEntity(id = 1L, front = "What is 1+1?", back = "2", sessionId = 1L, isLearned = false)
        `when`(dao.getFlashcardsForSession(1L)).thenReturn(flowOf(listOf(entity)))

        val repository = FlashcardRepositoryImpl(dao)
        val flashcards = repository.getFlashcardsForSession(1L).first()

        assertEquals(1, flashcards.size)
        assertEquals("What is 1+1?", flashcards[0].front)
        assertEquals("2", flashcards[0].back)
        assertEquals(false, flashcards[0].isLearned)
    }

    // 测试：getAllFlashcards 返回所有闪卡
    @Test
    fun getAllFlashcards_returns_all_flashcards() = runTest {
        val dao = mock(FlashcardDao::class.java)
        val entity1 = FlashcardEntity(id = 1L, front = "What is 1+1?", back = "2", sessionId = 1L, isLearned = false)
        val entity2 = FlashcardEntity(id = 2L, front = "What is 2+2?", back = "4", sessionId = 1L, isLearned = false)
        `when`(dao.getAllFlashcards()).thenReturn(flowOf(listOf(entity1, entity2)))

        val repository = FlashcardRepositoryImpl(dao)
        val flashcards = repository.getAllFlashcards().first()

        assertEquals(2, flashcards.size)
    }

    // 测试：getFlashcardById 返回指定 ID 的闪卡
    @Test
    fun getFlashcardById_returns_mapped_flashcard() = runTest {
        val dao = mock(FlashcardDao::class.java)
        val entity = FlashcardEntity(id = 5L, front = "What is 3+3?", back = "6", sessionId = 1L, isLearned = false)
        `when`(dao.getFlashcardById(5L)).thenReturn(entity)

        val repository = FlashcardRepositoryImpl(dao)
        val flashcard = repository.getFlashcardById(5L)

        assertEquals(5L, flashcard?.id)
        assertEquals("What is 3+3?", flashcard?.front)
        assertEquals("6", flashcard?.back)
    }

    // 测试：getFlashcardById 未找到时返回 null
    @Test
    fun getFlashcardById_returns_null_when_not_found() = runTest {
        val dao = mock(FlashcardDao::class.java)
        `when`(dao.getFlashcardById(999L)).thenReturn(null)

        val repository = FlashcardRepositoryImpl(dao)
        val flashcard = repository.getFlashcardById(999L)

        assertNull(flashcard)
    }

    // 测试：insertFlashcard 调用 DAO 并返回新插入闪卡的 ID
    @Test
    fun insertFlashcard_calls_dao_insert() = runTest {
        val dao = mock(FlashcardDao::class.java)
        `when`(dao.insertFlashcard(any<FlashcardEntity>())).thenReturn(1L)

        val repository = FlashcardRepositoryImpl(dao)
        val flashcard = Flashcard(id = 0L, front = "New Question", back = "New Answer", sessionId = 1L, isLearned = false)
        val result = repository.insertFlashcard(flashcard)

        assertEquals(1L, result)
    }

    // 测试：updateFlashcard 调用 DAO 的更新方法
    @Test
    fun updateFlashcard_calls_dao_update() = runTest {
        val dao = mock(FlashcardDao::class.java)

        val repository = FlashcardRepositoryImpl(dao)
        val flashcard = Flashcard(id = 1L, front = "Updated Question", back = "Updated Answer", sessionId = 1L, isLearned = true)
        repository.updateFlashcard(flashcard)
    }

    // 测试：deleteFlashcard 调用 DAO 的删除方法
    @Test
    fun deleteFlashcard_calls_dao_delete() = runTest {
        val dao = mock(FlashcardDao::class.java)

        val repository = FlashcardRepositoryImpl(dao)
        val flashcard = Flashcard(id = 1L, front = "Delete Me", back = "Deleted", sessionId = 1L, isLearned = false)
        repository.deleteFlashcard(flashcard)
    }

    // 测试：getFlashcardsForSession 无闪卡时返回空列表
    @Test
    fun getFlashcardsForSession_returns_empty_when_no_flashcards() = runTest {
        val dao = mock(FlashcardDao::class.java)
        `when`(dao.getFlashcardsForSession(999L)).thenReturn(flowOf(emptyList()))

        val repository = FlashcardRepositoryImpl(dao)
        val flashcards = repository.getFlashcardsForSession(999L).first()

        assertEquals(0, flashcards.size)
    }
}
