package com.study.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestionTest {
    @Test
    fun question_creates_choice_type() {
        val question = Question(
            id = 1L,
            subjectId = 1L,
            gradeId = 1L,
            type = QuestionType.CHOICE,
            content = "1+1=?",
            options = "[\"A: 1\", \"B: 2\", \"C: 3\", \"D: 4\"]",
            answer = "B"
        )
        assertEquals(1L, question.id)
        assertEquals(1L, question.subjectId)
        assertEquals(1L, question.gradeId)
        assertEquals(QuestionType.CHOICE, question.type)
        assertEquals("1+1=?", question.content)
        assertTrue(question.options!!.contains("A: 1"))
        assertEquals("B", question.answer)
        assertNull(question.hint)
    }

    @Test
    fun question_creates_fill_blank_type() {
        val question = Question(
            subjectId = 1L,
            gradeId = 1L,
            type = QuestionType.FILL_BLANK,
            content = "2+2=__",
            answer = "4"
        )
        assertEquals(QuestionType.FILL_BLANK, question.type)
        assertNull(question.options)
        assertNull(question.hint)
    }

    @Test
    fun question_creates_with_hint() {
        val question = Question(
            subjectId = 1L,
            gradeId = 1L,
            type = QuestionType.CHOICE,
            content = "首都是哪里?",
            options = "[\"A: 上海\", \"B: 北京\", \"C: 广州\", \"D: 深圳\"]",
            answer = "B",
            hint = "北方城市"
        )
        assertEquals("北方城市", question.hint)
    }

    @Test
    fun question_has_default_values() {
        val question = Question(
            type = QuestionType.FILL_BLANK,
            content = "test",
            answer = "answer"
        )
        assertEquals(0L, question.id)
        assertEquals(0L, question.subjectId)
        assertEquals(0L, question.gradeId)
        assertNull(question.options)
        assertNull(question.hint)
    }
}