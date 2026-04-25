package com.study.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.domain.model.StudySession
import com.study.app.domain.usecase.CreateSessionUseCase
import com.study.app.domain.usecase.GetSessionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val sessions: List<StudySession> = emptyList(),
    val isLoading: Boolean = true,
    val showCreateDialog: Boolean = false
)

class HomeViewModel @Inject constructor(
    private val getSessionsUseCase: GetSessionsUseCase,
    private val createSessionUseCase: CreateSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val sessions: List<StudySession>
        get() = _uiState.value.sessions

    init {
        loadSessions()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            getSessionsUseCase().collect { sessionList ->
                _uiState.value = _uiState.value.copy(
                    sessions = sessionList,
                    isLoading = false
                )
            }
        }
    }

    fun showCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = true)
    }

    fun hideCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = false)
    }

    fun createSession(subject: String) {
        viewModelScope.launch {
            createSessionUseCase(subject)
            hideCreateDialog()
        }
    }
}