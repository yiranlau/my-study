package com.study.app.ui.screens.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.domain.model.Grade
import com.study.app.domain.model.PracticeRecord
import com.study.app.domain.model.Subject
import com.study.app.domain.repository.GradeRepository
import com.study.app.domain.repository.PracticeRepository
import com.study.app.domain.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ChildHistoryViewModel @Inject constructor(
    private val practiceRepository: PracticeRepository,
    private val subjectRepository: SubjectRepository,
    private val gradeRepository: GradeRepository
) : ViewModel() {

    val practiceRecords: StateFlow<List<PracticeRecord>> = practiceRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val subjects: StateFlow<List<Subject>> = subjectRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val grades: StateFlow<List<Grade>> = gradeRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun getSubjectName(subjectId: Long): String? {
        return subjects.value.find { it.id == subjectId }?.name
    }

    fun getGradeName(gradeId: Long): String? {
        return grades.value.find { it.id == gradeId }?.name
    }

    fun getAccuracy(record: PracticeRecord): Float {
        if (record.totalQuestions == 0) return 0f
        return (record.correctCount.toFloat() / record.totalQuestions.toFloat()) * 100
    }

    fun formatDuration(durationMillis: Long): String {
        val totalSeconds = durationMillis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return when {
            hours > 0 -> "${hours}小时${minutes}分${seconds}秒"
            minutes > 0 -> "${minutes}分${seconds}秒"
            else -> "${seconds}秒"
        }
    }
}
