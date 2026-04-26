package com.study.app.ui.screens.parent

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

    val subjects: StateFlow<List<Subject>> = subjectRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun addSubject(name: String) {
        viewModelScope.launch {
            subjectRepository.insert(Subject(name = name))
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            subjectRepository.delete(subject)
        }
    }

    fun updateSubject(subject: Subject) {
        viewModelScope.launch {
            subjectRepository.update(subject)
        }
    }
}
