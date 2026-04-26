package com.study.app.ui.screens.quiz

import com.study.app.domain.model.Question
import com.study.app.domain.model.QuestionType
import com.study.app.domain.repository.PracticeRepository
import com.study.app.domain.repository.QuestionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    private lateinit var questionRepository: QuestionRepository
    private lateinit var practiceRepository: PracticeRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        questionRepository = mock(QuestionRepository::class.java)
        practiceRepository = mock(PracticeRepository::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): QuizViewModel {
        return QuizViewModel(questionRepository, practiceRepository)
    }

    @Test
    fun initial_state_has_empty_questions() {
        val viewModel = createViewModel()
        assertTrue(viewModel.state.value.questions.isEmpty())
        assertEquals(0, viewModel.state.value.currentIndex)
        assertTrue(viewModel.state.value.answers.isEmpty())
        assertFalse(viewModel.state.value.isFinished)
        assertEquals(0, viewModel.state.value.remainingSeconds)
    }

    @Test
    fun currentQuestion_returns_null_when_no_questions() {
        val viewModel = createViewModel()
        assertNull(viewModel.currentQuestion)
    }

    @Test
    fun loadQuestions_sets_questions() = runTest {
        val questions = listOf(
            Question(id = 1L, subjectId = 1L, gradeId = 1L, type = QuestionType.CHOICE, content = "Q1", answer = "A"),
            Question(id = 2L, subjectId = 1L, gradeId = 1L, type = QuestionType.CHOICE, content = "Q2", answer = "B")
        )
        `when`(questionRepository.getRandomQuestions(1L, 1L, 10)).thenReturn(questions)

        val viewModel = createViewModel()
        viewModel.loadQuestions(1L, 1L, 10)
        testDispatcher.scheduler.runCurrent()

        assertEquals(2, viewModel.state.value.questions.size)
        assertEquals("Q1", viewModel.state.value.questions[0].content)
    }

    @Test
    fun currentQuestion_returns_current_question() = runTest {
        val questions = listOf(
            Question(id = 1L, subjectId = 1L, gradeId = 1L, type = QuestionType.CHOICE, content = "Q1", answer = "A"),
            Question(id = 2L, subjectId = 1L, gradeId = 1L, type = QuestionType.CHOICE, content = "Q2", answer = "B")
        )
        `when`(questionRepository.getRandomQuestions(1L, 1L, 10)).thenReturn(questions)

        val viewModel = createViewModel()
        viewModel.loadQuestions(1L, 1L, 10)
        testDispatcher.scheduler.runCurrent()

        assertEquals("Q1", viewModel.currentQuestion?.content)
    }

    @Test
    fun answerQuestion_stores_answer() = runTest {
        val questions = listOf(
            Question(id = 1L, subjectId = 1L, gradeId = 1L, type = QuestionType.CHOICE, content = "Q1", answer = "A")
        )
        `when`(questionRepository.getRandomQuestions(1L, 1L, 10)).thenReturn(questions)

        val viewModel = createViewModel()
        viewModel.loadQuestions(1L, 1L, 10)
        testDispatcher.scheduler.runCurrent()

        viewModel.answerQuestion("A")

        assertEquals("A", viewModel.state.value.answers[0])
    }

    @Test
    fun nextQuestion_moves_to_next_index() = runTest {
        val questions = listOf(
            Question(id = 1L, subjectId = 1L, gradeId = 1L, type = QuestionType.CHOICE, content = "Q1", answer = "A"),
            Question(id = 2L, subjectId = 1L, gradeId = 1L, type = QuestionType.CHOICE, content = "Q2", answer = "B")
        )
        `when`(questionRepository.getRandomQuestions(1L, 1L, 10)).thenReturn(questions)

        val viewModel = createViewModel()
        viewModel.loadQuestions(1L, 1L, 10)
        testDispatcher.scheduler.runCurrent()

        viewModel.nextQuestion()

        assertEquals(1, viewModel.state.value.currentIndex)
        assertEquals("Q2", viewModel.currentQuestion?.content)
    }

    @Test
    fun nextQuestion_sets_isFinished_when_last_question() = runTest {
        val questions = listOf(
            Question(id = 1L, subjectId = 1L, gradeId = 1L, type = QuestionType.CHOICE, content = "Q1", answer = "A")
        )
        `when`(questionRepository.getRandomQuestions(1L, 1L, 10)).thenReturn(questions)

        val viewModel = createViewModel()
        viewModel.loadQuestions(1L, 1L, 10)
        testDispatcher.scheduler.runCurrent()

        viewModel.nextQuestion()

        assertTrue(viewModel.state.value.isFinished)
    }

    @Test
    fun setTimeLimit_sets_remaining_seconds() {
        val viewModel = createViewModel()
        viewModel.setTimeLimit(60)

        assertEquals(60, viewModel.state.value.remainingSeconds)
    }

    @Test
    fun tickTimer_decrements_remaining_seconds() = runTest {
        val viewModel = createViewModel()
        viewModel.setTimeLimit(60)
        testDispatcher.scheduler.runCurrent()

        viewModel.tickTimer()

        assertEquals(59, viewModel.state.value.remainingSeconds)
    }

    @Test
    fun tickTimer_sets_isTimeUp_when_reaches_zero() = runTest {
        val viewModel = createViewModel()
        viewModel.setTimeLimit(1)
        testDispatcher.scheduler.runCurrent()

        viewModel.tickTimer()

        assertTrue(viewModel.state.value.isTimeUp)
    }

    @Test
    fun previousQuestion_moves_to_previous_index() = runTest {
        val questions = listOf(
            Question(id = 1L, subjectId = 1L, gradeId = 1L, type = QuestionType.CHOICE, content = "Q1", answer = "A"),
            Question(id = 2L, subjectId = 1L, gradeId = 1L, type = QuestionType.CHOICE, content = "Q2", answer = "B")
        )
        `when`(questionRepository.getRandomQuestions(1L, 1L, 10)).thenReturn(questions)

        val viewModel = createViewModel()
        viewModel.loadQuestions(1L, 1L, 10)
        testDispatcher.scheduler.runCurrent()
        viewModel.nextQuestion()

        viewModel.previousQuestion()

        assertEquals(0, viewModel.state.value.currentIndex)
    }
}
