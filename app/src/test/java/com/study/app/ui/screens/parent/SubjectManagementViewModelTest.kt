package com.study.app.ui.screens.parent

import com.study.app.domain.model.Subject
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
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class SubjectManagementViewModelTest {

    private lateinit var subjectRepository: SubjectRepository
    private lateinit var viewModel: SubjectManagementViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        subjectRepository = mock(SubjectRepository::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initial_state_has_empty_subjects_list() = runTest {
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        viewModel = SubjectManagementViewModel(subjectRepository)

        assertEquals(emptyList<Subject>(), viewModel.subjects.value)
    }

    @Test
    fun subjects_flow_from_repository() = runTest {
        val subjects = listOf(
            Subject(id = 1, name = "Math"),
            Subject(id = 2, name = "Science")
        )
        `when`(subjectRepository.getAll()).thenReturn(flowOf(subjects))
        viewModel = SubjectManagementViewModel(subjectRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.subjects.value.size)
        assertEquals("Math", viewModel.subjects.value[0].name)
        assertEquals("Science", viewModel.subjects.value[1].name)
    }

    @Test
    fun addSubject_calls_repository_insert() = runTest {
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(subjectRepository.insert(any())).thenReturn(1L)
        viewModel = SubjectManagementViewModel(subjectRepository)

        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.addSubject("Math")

        testDispatcher.scheduler.advanceUntilIdle()

        verify(subjectRepository).insert(any())
    }

    @Test
    fun deleteSubject_calls_repository_delete() = runTest {
        val subject = Subject(id = 1, name = "Math")
        `when`(subjectRepository.getAll()).thenReturn(flowOf(listOf(subject)))
        viewModel = SubjectManagementViewModel(subjectRepository)

        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.deleteSubject(subject)

        testDispatcher.scheduler.advanceUntilIdle()

        verify(subjectRepository).delete(subject)
    }

    @Test
    fun updateSubject_calls_repository_update() = runTest {
        val subject = Subject(id = 1, name = "Math")
        `when`(subjectRepository.getAll()).thenReturn(flowOf(listOf(subject)))
        viewModel = SubjectManagementViewModel(subjectRepository)

        testDispatcher.scheduler.advanceUntilIdle()
        val updatedSubject = subject.copy(name = "Advanced Math")
        viewModel.updateSubject(updatedSubject)

        testDispatcher.scheduler.advanceUntilIdle()

        verify(subjectRepository).update(updatedSubject)
    }
}
