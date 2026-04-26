package com.study.app.ui.screens.result

import com.study.app.domain.model.Grade
import com.study.app.domain.model.PracticeRecord
import com.study.app.domain.model.Subject
import com.study.app.domain.repository.GradeRepository
import com.study.app.domain.repository.PracticeRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class ChildHistoryViewModelTest {

    private lateinit var practiceRepository: PracticeRepository
    private lateinit var subjectRepository: SubjectRepository
    private lateinit var gradeRepository: GradeRepository
    private lateinit var viewModel: ChildHistoryViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        practiceRepository = mock(PracticeRepository::class.java)
        subjectRepository = mock(SubjectRepository::class.java)
        gradeRepository = mock(GradeRepository::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initial_state_has_empty_records_list() = runTest {
        `when`(practiceRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))
        viewModel = ChildHistoryViewModel(practiceRepository, subjectRepository, gradeRepository)

        assertEquals(emptyList<PracticeRecord>(), viewModel.practiceRecords.value)
    }

    @Test
    fun practice_records_flow_from_repository() = runTest {
        val records = listOf(
            PracticeRecord(
                id = 1,
                subjectId = 1,
                gradeId = 1,
                totalQuestions = 10,
                correctCount = 8,
                durationMillis = 60000,
                questionResults = "[]"
            ),
            PracticeRecord(
                id = 2,
                subjectId = 2,
                gradeId = 1,
                totalQuestions = 5,
                correctCount = 3,
                durationMillis = 30000,
                questionResults = "[]"
            )
        )
        `when`(practiceRepository.getAll()).thenReturn(flowOf(records))
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))
        viewModel = ChildHistoryViewModel(practiceRepository, subjectRepository, gradeRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.practiceRecords.value.size)
    }

    @Test
    fun getSubjectName_returns_subject_name_for_valid_id() = runTest {
        val subjects = listOf(
            Subject(id = 1, name = "数学"),
            Subject(id = 2, name = "语文")
        )
        `when`(practiceRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(subjectRepository.getAll()).thenReturn(flowOf(subjects))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))
        viewModel = ChildHistoryViewModel(practiceRepository, subjectRepository, gradeRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("数学", viewModel.getSubjectName(1))
        assertEquals("语文", viewModel.getSubjectName(2))
    }

    @Test
    fun getSubjectName_returns_null_for_invalid_id() = runTest {
        `when`(practiceRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))
        viewModel = ChildHistoryViewModel(practiceRepository, subjectRepository, gradeRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.getSubjectName(999))
    }

    @Test
    fun getGradeName_returns_grade_name_for_valid_id() = runTest {
        val grades = listOf(
            Grade(id = 1, name = "一年级"),
            Grade(id = 2, name = "二年级")
        )
        `when`(practiceRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(grades))
        viewModel = ChildHistoryViewModel(practiceRepository, subjectRepository, gradeRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("一年级", viewModel.getGradeName(1))
        assertEquals("二年级", viewModel.getGradeName(2))
    }

    @Test
    fun getGradeName_returns_null_for_invalid_id() = runTest {
        `when`(practiceRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))
        viewModel = ChildHistoryViewModel(practiceRepository, subjectRepository, gradeRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.getGradeName(999))
    }

    @Test
    fun getAccuracy_returns_correct_percentage() = runTest {
        val record = PracticeRecord(
            id = 1,
            subjectId = 1,
            gradeId = 1,
            totalQuestions = 10,
            correctCount = 8,
            durationMillis = 60000,
            questionResults = "[]"
        )
        `when`(practiceRepository.getAll()).thenReturn(flowOf(listOf(record)))
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))
        viewModel = ChildHistoryViewModel(practiceRepository, subjectRepository, gradeRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        val accuracy = viewModel.getAccuracy(record)
        assertEquals(80.0f, accuracy, 0.01f)
    }

    @Test
    fun getAccuracy_returns_zero_for_zero_questions() = runTest {
        val record = PracticeRecord(
            id = 1,
            subjectId = 1,
            gradeId = 1,
            totalQuestions = 0,
            correctCount = 0,
            durationMillis = 0,
            questionResults = "[]"
        )
        `when`(practiceRepository.getAll()).thenReturn(flowOf(listOf(record)))
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))
        viewModel = ChildHistoryViewModel(practiceRepository, subjectRepository, gradeRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        val accuracy = viewModel.getAccuracy(record)
        assertEquals(0.0f, accuracy, 0.01f)
    }

    @Test
    fun formatDuration_returns_correct_format() = runTest {
        `when`(practiceRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))
        viewModel = ChildHistoryViewModel(practiceRepository, subjectRepository, gradeRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        // 60 seconds
        assertEquals("1分0秒", viewModel.formatDuration(60000))
        // 90 seconds
        assertEquals("1分30秒", viewModel.formatDuration(90000))
        // 3661 seconds (1 hour 1 minute 1 second)
        assertEquals("1小时1分1秒", viewModel.formatDuration(3661000))
        // Less than a minute
        assertEquals("30秒", viewModel.formatDuration(30000))
    }

    @Test
    fun total_practice_count_calculated_correctly() = runTest {
        val records = listOf(
            PracticeRecord(
                id = 1,
                subjectId = 1,
                gradeId = 1,
                totalQuestions = 10,
                correctCount = 8,
                durationMillis = 60000,
                questionResults = "[]"
            ),
            PracticeRecord(
                id = 2,
                subjectId = 2,
                gradeId = 1,
                totalQuestions = 5,
                correctCount = 3,
                durationMillis = 30000,
                questionResults = "[]"
            )
        )
        `when`(practiceRepository.getAll()).thenReturn(flowOf(records))
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))
        viewModel = ChildHistoryViewModel(practiceRepository, subjectRepository, gradeRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.practiceRecords.value.size)
        assertEquals(15, viewModel.practiceRecords.value.sumOf { it.totalQuestions })
    }

    @Test
    fun records_sorted_by_createdAt_descending() = runTest {
        val records = listOf(
            PracticeRecord(
                id = 1,
                subjectId = 1,
                gradeId = 1,
                totalQuestions = 10,
                correctCount = 8,
                durationMillis = 60000,
                questionResults = "[]",
                createdAt = 1000L
            ),
            PracticeRecord(
                id = 2,
                subjectId = 2,
                gradeId = 1,
                totalQuestions = 5,
                correctCount = 3,
                durationMillis = 30000,
                questionResults = "[]",
                createdAt = 2000L
            ),
            PracticeRecord(
                id = 3,
                subjectId = 1,
                gradeId = 2,
                totalQuestions = 8,
                correctCount = 6,
                durationMillis = 45000,
                questionResults = "[]",
                createdAt = 3000L
            )
        )
        `when`(practiceRepository.getAll()).thenReturn(flowOf(records))
        `when`(subjectRepository.getAll()).thenReturn(flowOf(emptyList()))
        `when`(gradeRepository.getAll()).thenReturn(flowOf(emptyList()))
        viewModel = ChildHistoryViewModel(practiceRepository, subjectRepository, gradeRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        val sortedRecords = viewModel.practiceRecords.value.sortedByDescending { it.createdAt }
        assertTrue(sortedRecords[0].createdAt >= sortedRecords[1].createdAt)
        assertTrue(sortedRecords[1].createdAt >= sortedRecords[2].createdAt)
    }
}
