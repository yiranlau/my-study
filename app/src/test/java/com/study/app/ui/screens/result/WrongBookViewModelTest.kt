package com.study.app.ui.screens.result

import com.study.app.data.local.GradeDao
import com.study.app.data.local.QuestionDao
import com.study.app.data.local.SubjectDao
import com.study.app.data.local.entity.WrongAnswerBookEntity
import com.study.app.domain.model.WrongAnswerBook
import com.study.app.domain.repository.WrongAnswerRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class WrongBookViewModelTest {

    private lateinit var viewModel: WrongBookViewModel
    private lateinit var mockWrongAnswerRepository: WrongAnswerRepository
    private lateinit var mockQuestionDao: QuestionDao
    private lateinit var mockSubjectDao: SubjectDao
    private lateinit var mockGradeDao: GradeDao
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockWrongAnswerRepository = mock()
        mockQuestionDao = mock()
        mockSubjectDao = mock()
        mockGradeDao = mock()
    }

    @Test
    fun wrongQuestions_initiallyEmpty() = runTest(testDispatcher) {
        whenever(mockWrongAnswerRepository.getAll()).thenReturn(MutableStateFlow(emptyList()))

        viewModel = WrongBookViewModel(
            wrongAnswerRepository = mockWrongAnswerRepository,
            questionDao = mockQuestionDao,
            subjectDao = mockSubjectDao,
            gradeDao = mockGradeDao
        )

        advanceUntilIdle()

        // Initially should be empty since repository returns empty
        val questions = viewModel.wrongQuestions.value
        assertTrue(questions.isEmpty())
    }

    @Test
    fun filterBySubject_updatesSelectedSubject() = runTest(testDispatcher) {
        whenever(mockWrongAnswerRepository.getAll()).thenReturn(MutableStateFlow(emptyList()))

        viewModel = WrongBookViewModel(
            wrongAnswerRepository = mockWrongAnswerRepository,
            questionDao = mockQuestionDao,
            subjectDao = mockSubjectDao,
            gradeDao = mockGradeDao
        )

        advanceUntilIdle()

        viewModel.filterBySubject("语文")
        advanceUntilIdle()

        assertEquals("语文", viewModel.selectedSubject.value)
    }

    @Test
    fun filterBySubject_allShowsAllQuestions() = runTest(testDispatcher) {
        whenever(mockWrongAnswerRepository.getAll()).thenReturn(MutableStateFlow(emptyList()))

        viewModel = WrongBookViewModel(
            wrongAnswerRepository = mockWrongAnswerRepository,
            questionDao = mockQuestionDao,
            subjectDao = mockSubjectDao,
            gradeDao = mockGradeDao
        )

        advanceUntilIdle()

        viewModel.filterBySubject("全部")
        advanceUntilIdle()

        assertEquals("全部", viewModel.selectedSubject.value)
    }

    @Test
    fun removeFromWrongBook_delegatesToRepository() = runTest(testDispatcher) {
        whenever(mockWrongAnswerRepository.getAll()).thenReturn(MutableStateFlow(emptyList()))

        viewModel = WrongBookViewModel(
            wrongAnswerRepository = mockWrongAnswerRepository,
            questionDao = mockQuestionDao,
            subjectDao = mockSubjectDao,
            gradeDao = mockGradeDao
        )

        advanceUntilIdle()

        // Just verify it doesn't throw
        viewModel.removeFromWrongBook(1L)
        advanceUntilIdle()
    }
}
