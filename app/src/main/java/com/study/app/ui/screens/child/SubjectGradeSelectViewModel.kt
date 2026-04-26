package com.study.app.ui.screens.child

import com.study.app.util.Logger
import com.study.app.util.PreferencesManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.domain.model.Grade
import com.study.app.domain.model.Subject
import com.study.app.domain.repository.GradeRepository
import com.study.app.domain.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectGradeSelectViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val gradeRepository: GradeRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val TAG = "VMSubjectGradeSelectViewModel"

    val subjects: StateFlow<List<Subject>> = subjectRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val grades: StateFlow<List<Grade>> = gradeRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _selectedSubject = MutableStateFlow<Subject?>(null)
    val selectedSubject: StateFlow<Subject?> = _selectedSubject.asStateFlow()

    private val _selectedGrade = MutableStateFlow<Grade?>(null)
    val selectedGrade: StateFlow<Grade?> = _selectedGrade.asStateFlow()

    init {
        viewModelScope.launch {
            subjects.collect { subjectList ->
                val lastSubjectId = preferencesManager.lastSubjectId
                if (lastSubjectId > 0) {
                    val subject = subjectList.find { it.id == lastSubjectId }
                    if (subject != null) {
                        _selectedSubject.value = subject
                        Logger.d(TAG, "Restored last selected subject: ${subject.name}")
                    }
                }
            }
        }
        viewModelScope.launch {
            grades.collect { gradeList ->
                val lastGradeId = preferencesManager.lastGradeId
                if (lastGradeId > 0) {
                    val grade = gradeList.find { it.id == lastGradeId }
                    if (grade != null) {
                        _selectedGrade.value = grade
                        Logger.d(TAG, "Restored last selected grade: ${grade.name}")
                    }
                }
            }
        }
    }

    fun selectSubject(subject: Subject) {
        Logger.d(TAG, "selectSubject: subjectId=${subject.id}, name=${subject.name}")
        _selectedSubject.value = subject
        preferencesManager.lastSubjectId = subject.id
    }

    fun selectGrade(grade: Grade) {
        Logger.d(TAG, "selectGrade: gradeId=${grade.id}, name=${grade.name}")
        _selectedGrade.value = grade
        preferencesManager.lastGradeId = grade.id
    }
}
