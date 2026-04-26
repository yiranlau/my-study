package com.study.app.ui.screens.child

import com.study.app.util.PreferencesManager
import com.study.app.domain.model.Grade
import com.study.app.domain.model.Subject
import com.study.app.domain.repository.GradeRepository
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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class SubjectGradeSelectViewModelTest {

    private lateinit var subjectRepository: SubjectRepository
    private lateinit var gradeRepository: GradeRepository
    private lateinit var preferencesManager: PreferencesManager
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        subjectRepository = mock(SubjectRepository::class.java)
        gradeRepository = mock(GradeRepository::class.java)
        preferencesManager = mock(PreferencesManager::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): SubjectGradeSelectViewModel {
        return SubjectGradeSelectViewModel(subjectRepository, gradeRepository, preferencesManager)
    }

    @Test
    fun initial_selected_subject_is_null() = runTest {
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))

        val viewModel = createViewModel()

        assertNull(viewModel.selectedSubject.value)
    }

    @Test
    fun initial_selected_grade_is_null() = runTest {
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))

        val viewModel = createViewModel()

        assertNull(viewModel.selectedGrade.value)
    }

    @Test
    fun subjects_flow_from_repository() = runTest {
        val subjects = listOf(
            Subject(id = 1, name = "语文"),
            Subject(id = 2, name = "数学")
        )
        `when`(subjectRepository.getAll()).thenReturn(flowOf(subjects))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))

        val viewModel = createViewModel()

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.subjects.value.size)
        assertEquals("语文", viewModel.subjects.value[0].name)
        assertEquals("数学", viewModel.subjects.value[1].name)
    }

    @Test
    fun grades_flow_from_repository() = runTest {
        val grades = listOf(
            Grade(id = 1, name = "一年级", order = 1),
            Grade(id = 2, name = "二年级", order = 2)
        )
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(grades))

        val viewModel = createViewModel()

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.grades.value.size)
        assertEquals("一年级", viewModel.grades.value[0].name)
        assertEquals("二年级", viewModel.grades.value[1].name)
    }

    @Test
    fun select_subject_updates_selected_subject() = runTest {
        val subject = Subject(id = 1, name = "语文")
        `when`(subjectRepository.getAll()).thenReturn(flowOf(listOf(subject)))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))

        val viewModel = createViewModel()

        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.selectSubject(subject)

        assertEquals(subject, viewModel.selectedSubject.value)
    }

    @Test
    fun select_grade_updates_selected_grade() = runTest {
        val grade = Grade(id = 1, name = "一年级", order = 1)
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(listOf(grade)))

        val viewModel = createViewModel()

        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.selectGrade(grade)

        assertEquals(grade, viewModel.selectedGrade.value)
    }

    @Test
    fun select_new_subject_replaces_previous_selection() = runTest {
        val subject1 = Subject(id = 1, name = "语文")
        val subject2 = Subject(id = 2, name = "数学")
        `when`(subjectRepository.getAll()).thenReturn(flowOf(listOf(subject1, subject2)))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))

        val viewModel = createViewModel()

        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.selectSubject(subject1)
        viewModel.selectSubject(subject2)

        assertEquals(subject2, viewModel.selectedSubject.value)
    }
}
