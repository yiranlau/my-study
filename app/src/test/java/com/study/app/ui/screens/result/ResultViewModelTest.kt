package com.study.app.ui.screens.result

import com.study.app.domain.model.Question
import com.study.app.domain.model.QuestionType
import com.study.app.domain.repository.WrongAnswerRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class ResultViewModelTest {

    private lateinit var viewModel: ResultViewModel
    private lateinit var mockWrongAnswerRepository: WrongAnswerRepository

    @Before
    fun setup() {
        mockWrongAnswerRepository = mock()
        viewModel = ResultViewModel(mockWrongAnswerRepository)
    }

    @Test
    fun calculateResults_withAllCorrect_calculatesCorrectStatistics() {
        val questions = listOf(
            Question(
                id = 1,
                subjectId = 1,
                gradeId = 1,
                type = QuestionType.CHOICE,
                content = "Question 1",
                options = "[\"A\",\"B\",\"C\",\"D\"]",
                answer = "A"
            ),
            Question(
                id = 2,
                subjectId = 1,
                gradeId = 1,
                type = QuestionType.CHOICE,
                content = "Question 2",
                options = "[\"A\",\"B\",\"C\",\"D\"]",
                answer = "B"
            )
        )

        val answers = mapOf(0 to "A", 1 to "B")

        viewModel.calculateResults(questions, answers, 60000L)

        val state = viewModel.uiState.value
        assertEquals(2, state.totalQuestions)
        assertEquals(2, state.correctCount)
        assertEquals(100f, state.accuracy, 0.01f)
        assertEquals(60000L, state.durationMillis)
        assertEquals(2, state.questionResults.size)
        assertTrue(state.questionResults[0].isCorrect)
        assertTrue(state.questionResults[1].isCorrect)
    }

    @Test
    fun calculateResults_withSomeCorrect_calculatesCorrectStatistics() {
        val questions = listOf(
            Question(
                id = 1,
                subjectId = 1,
                gradeId = 1,
                type = QuestionType.CHOICE,
                content = "Question 1",
                options = "[\"A\",\"B\",\"C\",\"D\"]",
                answer = "A"
            ),
            Question(
                id = 2,
                subjectId = 1,
                gradeId = 1,
                type = QuestionType.CHOICE,
                content = "Question 2",
                options = "[\"A\",\"B\",\"C\",\"D\"]",
                answer = "B"
            ),
            Question(
                id = 3,
                subjectId = 1,
                gradeId = 1,
                type = QuestionType.CHOICE,
                content = "Question 3",
                options = "[\"A\",\"B\",\"C\",\"D\"]",
                answer = "C"
            ),
            Question(
                id = 4,
                subjectId = 1,
                gradeId = 1,
                type = QuestionType.CHOICE,
                content = "Question 4",
                options = "[\"A\",\"B\",\"C\",\"D\"]",
                answer = "D"
            )
        )

        val answers = mapOf(0 to "A", 1 to "B", 2 to "A", 3 to "D")

        viewModel.calculateResults(questions, answers, 120000L)

        val state = viewModel.uiState.value
        assertEquals(4, state.totalQuestions)
        assertEquals(3, state.correctCount)
        assertEquals(75f, state.accuracy, 0.01f)
        assertEquals(120000L, state.durationMillis)
        assertEquals(4, state.questionResults.size)

        assertTrue(state.questionResults[0].isCorrect)
        assertTrue(state.questionResults[1].isCorrect)
        assertFalse(state.questionResults[2].isCorrect)
        assertTrue(state.questionResults[3].isCorrect)
    }

    @Test
    fun calculateResults_withAllWrong_calculatesZeroAccuracy() {
        val questions = listOf(
            Question(
                id = 1,
                subjectId = 1,
                gradeId = 1,
                type = QuestionType.CHOICE,
                content = "Question 1",
                options = "[\"A\",\"B\",\"C\",\"D\"]",
                answer = "A"
            ),
            Question(
                id = 2,
                subjectId = 1,
                gradeId = 1,
                type = QuestionType.CHOICE,
                content = "Question 2",
                options = "[\"A\",\"B\",\"C\",\"D\"]",
                answer = "B"
            )
        )

        val answers = mapOf(0 to "B", 1 to "A")

        viewModel.calculateResults(questions, answers, 30000L)

        val state = viewModel.uiState.value
        assertEquals(2, state.totalQuestions)
        assertEquals(0, state.correctCount)
        assertEquals(0f, state.accuracy, 0.01f)
        assertEquals(2, state.questionResults.size)
        assertFalse(state.questionResults[0].isCorrect)
        assertFalse(state.questionResults[1].isCorrect)
    }

    @Test
    fun calculateResults_withMissingAnswers_marksAsWrong() {
        val questions = listOf(
            Question(
                id = 1,
                subjectId = 1,
                gradeId = 1,
                type = QuestionType.CHOICE,
                content = "Question 1",
                options = "[\"A\",\"B\",\"C\",\"D\"]",
                answer = "A"
            ),
            Question(
                id = 2,
                subjectId = 1,
                gradeId = 1,
                type = QuestionType.CHOICE,
                content = "Question 2",
                options = "[\"A\",\"B\",\"C\",\"D\"]",
                answer = "B"
            )
        )

        // Only answer question 1, question 2 is not answered
        val answers = mapOf(0 to "A")

        viewModel.calculateResults(questions, answers, 45000L)

        val state = viewModel.uiState.value
        assertEquals(2, state.totalQuestions)
        assertEquals(1, state.correctCount)
        assertEquals(50f, state.accuracy, 0.01f)

        assertTrue(state.questionResults[0].isCorrect)
        assertFalse(state.questionResults[1].isCorrect)
        assertEquals("", state.questionResults[1].userAnswer)
    }

    @Test
    fun calculateResults_withEmptyQuestions_returnsEmptyResults() {
        viewModel.calculateResults(emptyList(), emptyMap(), 0L)

        val state = viewModel.uiState.value
        assertEquals(0, state.totalQuestions)
        assertEquals(0, state.correctCount)
        assertEquals(0f, state.accuracy, 0.01f)
        assertTrue(state.questionResults.isEmpty())
    }

    @Test
    fun calculateResults_preservesUserAnswer() {
        val questions = listOf(
            Question(
                id = 1,
                subjectId = 1,
                gradeId = 1,
                type = QuestionType.FILL_BLANK,
                content = "Fill in blank",
                answer = "answer"
            )
        )

        val answers = mapOf(0 to "my_answer")

        viewModel.calculateResults(questions, answers, 10000L)

        val state = viewModel.uiState.value
        assertEquals("my_answer", state.questionResults[0].userAnswer)
    }

    @Test
    fun getAccuracy_returnsCorrectPercentage() {
        assertEquals(75f, viewModel.getAccuracy(3, 4), 0.01f)
        assertEquals(50f, viewModel.getAccuracy(1, 2), 0.01f)
        assertEquals(100f, viewModel.getAccuracy(10, 10), 0.01f)
        assertEquals(0f, viewModel.getAccuracy(0, 5), 0.01f)
    }

    @Test
    fun getAccuracy_withZeroTotal_returnsZero() {
        assertEquals(0f, viewModel.getAccuracy(0, 0), 0.01f)
    }
}
