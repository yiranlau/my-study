package com.study.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class WrongAnswerBookTest {
    @Test
    fun wrong_answer_book_creates_with_all_fields() {
        val timestamp = System.currentTimeMillis()
        val book = WrongAnswerBook(
            id = 1L,
            questionId = 100L,
            studentAnswer = "B",
            correctAnswer = "C",
            addedAt = timestamp
        )
        assertEquals(1L, book.id)
        assertEquals(100L, book.questionId)
        assertEquals("B", book.studentAnswer)
        assertEquals("C", book.correctAnswer)
        assertEquals(timestamp, book.addedAt)
    }

    @Test
    fun wrong_answer_book_has_default_values() {
        val book = WrongAnswerBook(
            questionId = 50L,
            studentAnswer = "A",
            correctAnswer = "D"
        )
        assertEquals(0L, book.id)
        assertEquals(50L, book.questionId)
        assertEquals("A", book.studentAnswer)
        assertEquals("D", book.correctAnswer)
        assertNotNull(book.addedAt)
    }

    @Test
    fun wrong_answer_book_stores_different_answer() {
        val book = WrongAnswerBook(
            questionId = 1L,
            studentAnswer = "wrong answer",
            correctAnswer = "right answer"
        )
        assertEquals("wrong answer", book.studentAnswer)
        assertEquals("right answer", book.correctAnswer)
        assert(book.studentAnswer != book.correctAnswer)
    }
}