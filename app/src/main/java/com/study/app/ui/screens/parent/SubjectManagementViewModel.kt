package com.study.app.ui.screens.parent

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.domain.model.Subject
import com.study.app.domain.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectManagementViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val TAG = "VMSubjectManagementViewModel"

    val subjects: StateFlow<List<Subject>> = subjectRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun addSubject(name: String) {
        Log.d(TAG, "addSubject: name=$name")
        viewModelScope.launch {
            subjectRepository.insert(Subject(name = name))
            Log.d(TAG, "addSubject: subject added successfully")
        }
    }

    fun deleteSubject(subject: Subject) {
        Log.d(TAG, "deleteSubject: subjectId=${subject.id}, name=${subject.name}")
        viewModelScope.launch {
            subjectRepository.delete(subject)
            Log.d(TAG, "deleteSubject: subject deleted successfully")
        }
    }

    fun updateSubject(subject: Subject) {
        Log.d(TAG, "updateSubject: subjectId=${subject.id}, name=${subject.name}")
        viewModelScope.launch {
            subjectRepository.update(subject)
            Log.d(TAG, "updateSubject: subject updated successfully")
        }
    }
}
