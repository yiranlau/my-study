package com.study.app.ui.screens.parent

import android.util.Log
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

    private val TAG = "VMGradeManagementViewModel"

    val grades: StateFlow<List<Grade>> = gradeRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun addGrade(name: String, order: Int) {
        Log.d(TAG, "addGrade: name=$name, order=$order")
        viewModelScope.launch {
            gradeRepository.insert(Grade(name = name, order = order))
            Log.d(TAG, "addGrade: grade added successfully")
        }
    }

    fun deleteGrade(grade: Grade) {
        Log.d(TAG, "deleteGrade: gradeId=${grade.id}, name=${grade.name}")
        viewModelScope.launch {
            gradeRepository.delete(grade)
            Log.d(TAG, "deleteGrade: grade deleted successfully")
        }
    }

    fun updateGrade(grade: Grade) {
        Log.d(TAG, "updateGrade: gradeId=${grade.id}, name=${grade.name}")
        viewModelScope.launch {
            gradeRepository.update(grade)
            Log.d(TAG, "updateGrade: grade updated successfully")
        }
    }
}
