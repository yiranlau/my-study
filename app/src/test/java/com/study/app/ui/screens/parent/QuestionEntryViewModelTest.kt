package com.study.app.ui.screens.parent

import com.study.app.domain.model.Question
import com.study.app.domain.model.QuestionType
import com.study.app.domain.repository.GradeRepository
import com.study.app.domain.repository.QuestionRepository
import com.study.app.domain.repository.SubjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

@OptIn(ExperimentalCoroutinesApi::class)
class QuestionEntryViewModelTest {

    private lateinit var questionRepository: QuestionRepository
    private lateinit var subjectRepository: SubjectRepository
    private lateinit var gradeRepository: GradeRepository
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
    fun initial_state_has_choice_type() = runTest {
        `when`(questionRepository.getBySubjectAndGrade(any(), any())).thenReturn(flowOf(emptyList()))
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))

        val viewModel = QuestionEntryViewModel(questionRepository, subjectRepository, gradeRepository)

        assertEquals(QuestionType.CHOICE, viewModel.selectedType.value)
        assertEquals("", viewModel.content.value)
        assertEquals(listOf("", "", "", ""), viewModel.options.value)
        assertEquals("", viewModel.answer.value)
        assertEquals("", viewModel.hint.value)
    }

    @Test
    fun setType_changes_selected_type() = runTest {
        `when`(questionRepository.getBySubjectAndGrade(any(), any())).thenReturn(flowOf(emptyList()))
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))

        val viewModel = QuestionEntryViewModel(questionRepository, subjectRepository, gradeRepository)

        viewModel.setType(QuestionType.FILL_BLANK)
        assertEquals(QuestionType.FILL_BLANK, viewModel.selectedType.value)

        viewModel.setType(QuestionType.CHOICE)
        assertEquals(QuestionType.CHOICE, viewModel.selectedType.value)
    }

    @Test
    fun saveQuestion_inserts_choice_question() = runTest {
        `when`(questionRepository.getBySubjectAndGrade(1L, 1L)).thenReturn(flowOf(emptyList()))
        `when`(questionRepository.insert(any())).thenReturn(1L)
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))

        val viewModel = QuestionEntryViewModel(questionRepository, subjectRepository, gradeRepository)

        viewModel.setSubjectAndGrade(1L, 1L)
        viewModel.setType(QuestionType.CHOICE)
        viewModel.content.value = "1+1=?"
        viewModel.options.value = listOf("A: 1", "B: 2", "C: 3", "D: 4")
        viewModel.answer.value = "B"

        viewModel.saveQuestion()
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @Test
    fun saveQuestion_inserts_fill_blank_question() = runTest {
        `when`(questionRepository.getBySubjectAndGrade(1L, 1L)).thenReturn(flowOf(emptyList()))
        `when`(questionRepository.insert(any())).thenReturn(1L)
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))

        val viewModel = QuestionEntryViewModel(questionRepository, subjectRepository, gradeRepository)

        viewModel.setSubjectAndGrade(1L, 1L)
        viewModel.setType(QuestionType.FILL_BLANK)
        viewModel.content.value = "2+2=__"
        viewModel.answer.value = "4"
        viewModel.hint.value = "think about addition"

        viewModel.saveQuestion()
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @Test
    fun questions_flow_from_repository() = runTest {
        val questions = listOf(
            Question(id = 1L, subjectId = 1L, gradeId = 1L, type = QuestionType.CHOICE, content = "Q1", answer = "A"),
            Question(id = 2L, subjectId = 1L, gradeId = 1L, type = QuestionType.FILL_BLANK, content = "Q2", answer = "B")
        )
        `when`(questionRepository.getBySubjectAndGrade(1L, 1L)).thenReturn(flowOf(questions))
        `when`(questionRepository.getBySubjectAndGrade(0L, 0L)).thenReturn(flowOf(emptyList()))
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))

        val viewModel = QuestionEntryViewModel(questionRepository, subjectRepository, gradeRepository)

        viewModel.setSubjectAndGrade(1L, 1L)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.questions.value.size)
    }
}
