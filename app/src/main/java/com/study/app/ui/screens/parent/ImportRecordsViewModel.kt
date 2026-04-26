package com.study.app.ui.screens.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.domain.model.ImportRecord
import com.study.app.domain.repository.ImportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

enum class DateFilter {
    ALL, TODAY, THIS_WEEK, THIS_MONTH
}

@HiltViewModel
class ImportRecordsViewModel @Inject constructor(
    private val importRepository: ImportRepository
) : ViewModel() {

    private val _dateFilter = MutableStateFlow(DateFilter.ALL)
    val dateFilter: StateFlow<DateFilter> = _dateFilter.asStateFlow()

    val importRecords: StateFlow<List<ImportRecord>> = importRepository.getRecent(Int.MAX_VALUE)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val filteredRecords: StateFlow<List<ImportRecord>> = combine(
        importRecords,
        _dateFilter
    ) { records, filter ->
        filterRecords(records, filter)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val totalImportCount: Int
        get() = importRecords.value.size

    val totalQuestionsImported: Int
        get() = importRecords.value.sumOf { it.successCount }

    fun setDateFilter(filter: DateFilter) {
        _dateFilter.value = filter
    }

    fun deleteRecord(record: ImportRecord) {
        viewModelScope.launch {
            importRepository.delete(record)
        }
    }

    private fun filterRecords(records: List<ImportRecord>, filter: DateFilter): List<ImportRecord> {
        if (filter == DateFilter.ALL) {
            return records
        }

        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        return when (filter) {
            DateFilter.ALL -> records
            DateFilter.TODAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val todayStart = calendar.timeInMillis
                records.filter { it.createdAt >= todayStart }
            }
            DateFilter.THIS_WEEK -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val weekStart = calendar.timeInMillis
                records.filter { it.createdAt >= weekStart }
            }
            DateFilter.THIS_MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val monthStart = calendar.timeInMillis
                records.filter { it.createdAt >= monthStart }
            }
        }
    }
}
