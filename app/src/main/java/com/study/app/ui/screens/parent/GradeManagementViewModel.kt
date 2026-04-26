package com.study.app.ui.screens.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.domain.model.Grade
import com.study.app.domain.repository.GradeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GradeManagementViewModel @Inject constructor(
    private val gradeRepository: GradeRepository
) : ViewModel() {

    val grades: StateFlow<List<Grade>> = gradeRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun addGrade(name: String, order: Int) {
        viewModelScope.launch {
            gradeRepository.insert(Grade(name = name, order = order))
        }
    }

    fun deleteGrade(grade: Grade) {
        viewModelScope.launch {
            gradeRepository.delete(grade)
        }
    }

    fun updateGrade(grade: Grade) {
        viewModelScope.launch {
            gradeRepository.update(grade)
        }
    }
}
