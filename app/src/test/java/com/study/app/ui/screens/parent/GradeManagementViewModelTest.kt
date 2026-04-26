package com.study.app.ui.screens.parent

import com.study.app.domain.model.Grade
import com.study.app.domain.repository.GradeRepository
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
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class GradeManagementViewModelTest {

    private lateinit var gradeRepository: GradeRepository
    private lateinit var viewModel: GradeManagementViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        gradeRepository = mock(GradeRepository::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initial_state_has_empty_grades_list() = runTest {
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))
        viewModel = GradeManagementViewModel(gradeRepository)

        assertEquals(emptyList<Grade>(), viewModel.grades.value)
    }

    @Test
    fun grades_flow_from_repository() = runTest {
        val grades = listOf(
            Grade(id = 1, name = "一年级", order = 1),
            Grade(id = 2, name = "二年级", order = 2)
        )
        `when`(gradeRepository.getAll()).thenReturn(flowOf(grades))
        viewModel = GradeManagementViewModel(gradeRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.grades.value.size)
        assertEquals("一年级", viewModel.grades.value[0].name)
        assertEquals("二年级", viewModel.grades.value[1].name)
    }

    @Test
    fun addGrade_calls_repository_insert() = runTest {
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.insert(any())).thenReturn(1L)
        viewModel = GradeManagementViewModel(gradeRepository)

        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.addGrade("三年级", 3)

        testDispatcher.scheduler.advanceUntilIdle()

        verify(gradeRepository).insert(any())
    }

    @Test
    fun deleteGrade_calls_repository_delete() = runTest {
        val grade = Grade(id = 1, name = "一年级", order = 1)
        `when`(gradeRepository.getAll()).thenReturn(flowOf(listOf(grade)))
        viewModel = GradeManagementViewModel(gradeRepository)

        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.deleteGrade(grade)

        testDispatcher.scheduler.advanceUntilIdle()

        verify(gradeRepository).delete(grade)
    }

    @Test
    fun updateGrade_calls_repository_update() = runTest {
        val grade = Grade(id = 1, name = "一年级", order = 1)
        `when`(gradeRepository.getAll()).thenReturn(flowOf(listOf(grade)))
        viewModel = GradeManagementViewModel(gradeRepository)

        testDispatcher.scheduler.advanceUntilIdle()
        val updatedGrade = grade.copy(name = "一年级（已修改）")
        viewModel.updateGrade(updatedGrade)

        testDispatcher.scheduler.advanceUntilIdle()

        verify(gradeRepository).update(updatedGrade)
    }
}
