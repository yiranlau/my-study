package com.study.app.ui.screens.parent

import com.study.app.domain.model.ImportRecord
import com.study.app.domain.repository.ImportRepository
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
class ImportRecordsViewModelTest {

    private lateinit var importRepository: ImportRepository
    private lateinit var viewModel: ImportRecordsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        importRepository = mock(ImportRepository::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initial_state_has_empty_records_list() = runTest {
        `when`(importRepository.getRecent(Int.MAX_VALUE)).thenReturn(flowOf(emptyList()))
        viewModel = ImportRecordsViewModel(importRepository)

        assertEquals(emptyList<ImportRecord>(), viewModel.importRecords.value)
    }

    @Test
    fun import_records_flow_from_repository() = runTest {
        val records = listOf(
            ImportRecord(id = 1, fileName = "math.csv", totalCount = 10, successCount = 8, failCount = 2, createdAt = System.currentTimeMillis()),
            ImportRecord(id = 2, fileName = "chinese.csv", totalCount = 5, successCount = 5, failCount = 0, createdAt = System.currentTimeMillis())
        )
        `when`(importRepository.getRecent(Int.MAX_VALUE)).thenReturn(flowOf(records))
        viewModel = ImportRecordsViewModel(importRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.importRecords.value.size)
        assertEquals("math.csv", viewModel.importRecords.value[0].fileName)
        assertEquals("chinese.csv", viewModel.importRecords.value[1].fileName)
    }

    @Test
    fun delete_record_calls_repository_delete() = runTest {
        val record = ImportRecord(id = 1, fileName = "math.csv", totalCount = 10, successCount = 8, failCount = 2, createdAt = System.currentTimeMillis())
        `when`(importRepository.getRecent(Int.MAX_VALUE)).thenReturn(flowOf(listOf(record)))
        viewModel = ImportRecordsViewModel(importRepository)

        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.deleteRecord(record)

        testDispatcher.scheduler.advanceUntilIdle()

        verify(importRepository).delete(record)
    }

    @Test
    fun total_import_count_returns_correct_count() = runTest {
        val records = listOf(
            ImportRecord(id = 1, fileName = "math.csv", totalCount = 10, successCount = 8, failCount = 2, createdAt = System.currentTimeMillis()),
            ImportRecord(id = 2, fileName = "chinese.csv", totalCount = 5, successCount = 5, failCount = 0, createdAt = System.currentTimeMillis())
        )
        `when`(importRepository.getRecent(Int.MAX_VALUE)).thenReturn(flowOf(records))
        viewModel = ImportRecordsViewModel(importRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.totalImportCount)
    }

    @Test
    fun total_questions_imported_returns_correct_count() = runTest {
        val records = listOf(
            ImportRecord(id = 1, fileName = "math.csv", totalCount = 10, successCount = 8, failCount = 2, createdAt = System.currentTimeMillis()),
            ImportRecord(id = 2, fileName = "chinese.csv", totalCount = 5, successCount = 5, failCount = 0, createdAt = System.currentTimeMillis())
        )
        `when`(importRepository.getRecent(Int.MAX_VALUE)).thenReturn(flowOf(records))
        viewModel = ImportRecordsViewModel(importRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        // Total questions imported is sum of successCount
        assertEquals(13, viewModel.totalQuestionsImported)
    }

    @Test
    fun set_date_filter_updates_filter_state() = runTest {
        `when`(importRepository.getRecent(Int.MAX_VALUE)).thenReturn(flowOf(emptyList()))
        viewModel = ImportRecordsViewModel(importRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setDateFilter(DateFilter.TODAY)

        assertEquals(DateFilter.TODAY, viewModel.dateFilter.value)
    }

    @Test
    fun filtered_records_returns_today_records_only() = runTest {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L
        val records = listOf(
            ImportRecord(id = 1, fileName = "today.csv", totalCount = 10, successCount = 10, failCount = 0, createdAt = now),
            ImportRecord(id = 2, fileName = "yesterday.csv", totalCount = 5, successCount = 5, failCount = 0, createdAt = now - oneDayMillis - 1)
        )
        `when`(importRepository.getRecent(Int.MAX_VALUE)).thenReturn(flowOf(records))
        viewModel = ImportRecordsViewModel(importRepository)

        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.setDateFilter(DateFilter.TODAY)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.filteredRecords.value.size)
        assertEquals("today.csv", viewModel.filteredRecords.value[0].fileName)
    }

    @Test
    fun filtered_records_returns_this_week_records_only() = runTest {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L
        // Record from very old (50 days ago) which should always be excluded from THIS_WEEK
        val records = listOf(
            ImportRecord(id = 1, fileName = "today.csv", totalCount = 10, successCount = 10, failCount = 0, createdAt = now),
            ImportRecord(id = 2, fileName = "old_record.csv", totalCount = 5, successCount = 5, failCount = 0, createdAt = now - 50 * oneDayMillis)
        )
        `when`(importRepository.getRecent(Int.MAX_VALUE)).thenReturn(flowOf(records))
        viewModel = ImportRecordsViewModel(importRepository)

        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.setDateFilter(DateFilter.THIS_WEEK)

        testDispatcher.scheduler.advanceUntilIdle()

        // Should only include today.csv, not the 50-day-old record
        assertEquals(1, viewModel.filteredRecords.value.size)
        assertEquals("today.csv", viewModel.filteredRecords.value[0].fileName)
    }

    @Test
    fun filtered_records_returns_this_month_records_only() = runTest {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L
        val records = listOf(
            ImportRecord(id = 1, fileName = "today.csv", totalCount = 10, successCount = 10, failCount = 0, createdAt = now),
            ImportRecord(id = 2, fileName = "this_month.csv", totalCount = 5, successCount = 5, failCount = 0, createdAt = now - 15 * oneDayMillis),
            ImportRecord(id = 3, fileName = "last_month.csv", totalCount = 3, successCount = 3, failCount = 0, createdAt = now - 45 * oneDayMillis)
        )
        `when`(importRepository.getRecent(Int.MAX_VALUE)).thenReturn(flowOf(records))
        viewModel = ImportRecordsViewModel(importRepository)

        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.setDateFilter(DateFilter.THIS_MONTH)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.filteredRecords.value.size)
    }

    @Test
    fun filtered_records_returns_all_when_filter_is_all() = runTest {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L
        val records = listOf(
            ImportRecord(id = 1, fileName = "today.csv", totalCount = 10, successCount = 10, failCount = 0, createdAt = now),
            ImportRecord(id = 2, fileName = "old.csv", totalCount = 5, successCount = 5, failCount = 0, createdAt = now - 100 * oneDayMillis)
        )
        `when`(importRepository.getRecent(Int.MAX_VALUE)).thenReturn(flowOf(records))
        viewModel = ImportRecordsViewModel(importRepository)

        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.setDateFilter(DateFilter.ALL)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.filteredRecords.value.size)
    }
}
