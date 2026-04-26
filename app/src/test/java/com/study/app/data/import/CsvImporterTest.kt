package com.study.app.data.import

import com.study.app.domain.model.Question
import com.study.app.domain.model.QuestionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CsvImporterTest {

    private val importer = CsvImporter()

    @Test
    fun parseLine_parses_choice_question() {
        val line = "CHOICE,数学,一年级,1+1=?,[\"2\",\"3\",\"4\",\"5\"],A"
        val result = importer.parseLine(line)

        assertTrue(result is CsvImportResult.Success)
        val success = result as CsvImportResult.Success
        val question = success.question

        assertEquals("数学", success.subjectName)
        assertEquals("一年级", success.gradeName)
        assertEquals(QuestionType.CHOICE, question.type)
        assertEquals(0L, question.subjectId)
        assertEquals(0L, question.gradeId)
        assertEquals("1+1=?", question.content)
        assertEquals("[\"2\",\"3\",\"4\",\"5\"]", question.options)
        assertEquals("A", question.answer)
    }

    @Test
    fun parseLine_parses_fill_blank_question() {
        val line = "FILL_BLANK,语文,二年级,中国的首都是?,,北京"
        val result = importer.parseLine(line)

        assertTrue(result is CsvImportResult.Success)
        val success = result as CsvImportResult.Success
        val question = success.question

        assertEquals("语文", success.subjectName)
        assertEquals("二年级", success.gradeName)
        assertEquals(QuestionType.FILL_BLANK, question.type)
        assertEquals(0L, question.subjectId)
        assertEquals(0L, question.gradeId)
        assertEquals("中国的首都是?", question.content)
        assertNull(question.options)
        assertEquals("北京", question.answer)
    }

    @Test
    fun parseLine_returns_error_for_unknown_type() {
        val line = "UNKNOWN,数学,一年级,test,,"
        val result = importer.parseLine(line)

        assertTrue(result is CsvImportResult.Error)
        val error = result as CsvImportResult.Error
        assertTrue(error.message.contains("Unknown type"))
        assertEquals(line, error.line)
    }

    @Test
    fun parseLine_returns_error_for_invalid_format() {
        val line = "CHOICE,数学"
        val result = importer.parseLine(line)

        assertTrue(result is CsvImportResult.Error)
        val error = result as CsvImportResult.Error
        assertTrue(error.message.contains("Invalid format"))
        assertEquals(line, error.line)
    }

    @Test
    fun parseLine_handles_lowercase_type() {
        val line = "fill_blank,数学,一年级,1+1=,,2"
        val result = importer.parseLine(line)

        assertTrue(result is CsvImportResult.Success)
        val success = result as CsvImportResult.Success
        assertEquals(QuestionType.FILL_BLANK, success.question.type)
        assertEquals("数学", success.subjectName)
        assertEquals("2", success.question.answer)
    }
}