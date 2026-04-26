package com.study.app.ui.screens.parent

import com.study.app.data.import.CsvImportResult
import com.study.app.data.import.CsvImporter
import com.study.app.domain.model.Question
import com.study.app.domain.model.QuestionType
import com.study.app.domain.repository.GradeRepository
import com.study.app.domain.repository.QuestionRepository
import com.study.app.domain.repository.SubjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class CsvImportViewModelTest {

    private lateinit var questionRepository: QuestionRepository
    private lateinit var subjectRepository: SubjectRepository
    private lateinit var gradeRepository: GradeRepository
    private lateinit var viewModel: CsvImportViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        questionRepository = mock(QuestionRepository::class.java)
        subjectRepository = mock(SubjectRepository::class.java)
        gradeRepository = mock(GradeRepository::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun parseFile_parses_valid_csv_lines() = runTest {
        viewModel = CsvImportViewModel(questionRepository, subjectRepository, gradeRepository)

        val csvContent = """type,subject,grade,content,options,answer
CHOICE,数学,一年级,1+1=?,["2","3","4","5"],A
FILL_BLANK,语文,二年级,中国的首都是?,,北京"""

        viewModel.parseFile(csvContent)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.importResults.value.size)
        assertTrue(viewModel.importResults.value[0] is CsvImportResult.Success)
        assertTrue(viewModel.importResults.value[1] is CsvImportResult.Success)

        val success1 = viewModel.importResults.value[0] as CsvImportResult.Success
        assertEquals("数学", success1.subjectName)
        assertEquals("一年级", success1.gradeName)
        assertEquals(QuestionType.CHOICE, success1.question.type)
        assertEquals("1+1=?", success1.question.content)
    }

    @Test
    fun parseFile_handles_error_results() = runTest {
        viewModel = CsvImportViewModel(questionRepository, subjectRepository, gradeRepository)

        val csvContent = """type,subject,grade,content,options,answer
INVALID,数学,一年级,test,,,"""

        viewModel.parseFile(csvContent)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.importResults.value.size)
        assertTrue(viewModel.importResults.value[0] is CsvImportResult.Error)
    }

    @Test
    fun parseFile_skips_header_line() = runTest {
        viewModel = CsvImportViewModel(questionRepository, subjectRepository, gradeRepository)

        val csvContent = """type,subject,grade,content,options,answer
CHOICE,数学,一年级,1+1=?,["2","3","4","5"],A"""

        viewModel.parseFile(csvContent)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.importResults.value.size)
        assertTrue(viewModel.importResults.value[0] is CsvImportResult.Success)
    }

    @Test
    fun confirmImport_inserts_only_success_results() = runTest {
        viewModel = CsvImportViewModel(questionRepository, subjectRepository, gradeRepository)

        val successQuestion = Question(
            subjectId = 0,
            gradeId = 0,
            type = QuestionType.CHOICE,
            content = "1+1=?",
            options = "[\"2\",\"3\",\"4\",\"5\"]",
            answer = "A"
        )
        val errorResult = CsvImportResult.Error("Invalid format", "INVALID,data")

        viewModel.setImportResultsForTest(listOf(
            CsvImportResult.Success("数学", "一年级", successQuestion),
            errorResult
        ))

        `when`(questionRepository.insert(any())).thenReturn(1L)

        viewModel.confirmImport(1L, 1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Should only insert the success result
        verify(questionRepository).insert(any())
    }

    @Test
    fun confirmImport_sets_subject_and_grade_ids() = runTest {
        viewModel = CsvImportViewModel(questionRepository, subjectRepository, gradeRepository)

        val successQuestion = Question(
            subjectId = 0,
            gradeId = 0,
            type = QuestionType.CHOICE,
            content = "1+1=?",
            options = "[\"2\",\"3\",\"4\",\"5\"]",
            answer = "A"
        )

        viewModel.setImportResultsForTest(listOf(
            CsvImportResult.Success("数学", "一年级", successQuestion)
        ))

        var insertedQuestion: Question? = null
        `when`(questionRepository.insert(any())).thenAnswer { invocation ->
            insertedQuestion = invocation.getArgument(0)
            1L
        }

        viewModel.confirmImport(5L, 10L)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(5L, insertedQuestion?.subjectId)
        assertEquals(10L, insertedQuestion?.gradeId)
    }

    @Test
    fun clearResults_clears_import_results() = runTest {
        viewModel = CsvImportViewModel(questionRepository, subjectRepository, gradeRepository)

        val successQuestion = Question(
            subjectId = 0,
            gradeId = 0,
            type = QuestionType.CHOICE,
            content = "1+1=?",
            options = "[\"2\",\"3\",\"4\",\"5\"]",
            answer = "A"
        )
        viewModel.setImportResultsForTest(listOf(
            CsvImportResult.Success("数学", "一年级", successQuestion)
        ))

        viewModel.clearResults()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.importResults.value.isEmpty())
    }

    @Test
    fun success_count_returns_correct_count() = runTest {
        viewModel = CsvImportViewModel(questionRepository, subjectRepository, gradeRepository)

        val successQuestion = Question(
            subjectId = 0,
            gradeId = 0,
            type = QuestionType.CHOICE,
            content = "1+1=?",
            options = "[\"2\",\"3\",\"4\",\"5\"]",
            answer = "A"
        )
        viewModel.setImportResultsForTest(listOf(
            CsvImportResult.Success("数学", "一年级", successQuestion),
            CsvImportResult.Success("语文", "一年级", successQuestion),
            CsvImportResult.Error("Invalid", "INVALID")
        ))

        assertEquals(2, viewModel.successCount)
    }

    @Test
    fun error_count_returns_correct_count() = runTest {
        viewModel = CsvImportViewModel(questionRepository, subjectRepository, gradeRepository)

        val successQuestion = Question(
            subjectId = 0,
            gradeId = 0,
            type = QuestionType.CHOICE,
            content = "1+1=?",
            options = "[\"2\",\"3\",\"4\",\"5\"]",
            answer = "A"
        )
        viewModel.setImportResultsForTest(listOf(
            CsvImportResult.Success("数学", "一年级", successQuestion),
            CsvImportResult.Error("Invalid1", "INVALID1"),
            CsvImportResult.Error("Invalid2", "INVALID2")
        ))

        assertEquals(2, viewModel.errorCount)
    }

    @Test
    fun parseFile_handles_empty_content() = runTest {
        viewModel = CsvImportViewModel(questionRepository, subjectRepository, gradeRepository)

        viewModel.parseFile("")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.importResults.value.isEmpty())
    }

    @Test
    fun parseFile_handles_only_header() = runTest {
        viewModel = CsvImportViewModel(questionRepository, subjectRepository, gradeRepository)

        val csvContent = """type,subject,grade,content,options,answer"""

        viewModel.parseFile(csvContent)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.importResults.value.isEmpty())
    }
}
