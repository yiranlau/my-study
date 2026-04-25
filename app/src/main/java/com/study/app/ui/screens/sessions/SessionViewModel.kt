package com.study.app.ui.screens.sessions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.domain.model.StudySession
import com.study.app.domain.usecase.EndSessionUseCase
import com.study.app.domain.usecase.GetFlashcardsUseCase
import com.study.app.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionUiState(
    val session: StudySession? = null,
    val flashcards: List<com.study.app.domain.model.Flashcard> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class SessionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionRepository: SessionRepository,
    private val endSessionUseCase: EndSessionUseCase,
    private val getFlashcardsUseCase: GetFlashcardsUseCase
) : ViewModel() {

    private val sessionId: Long = savedStateHandle.get<String>("sessionId")?.toLongOrNull() ?: 0L

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        loadSession()
        loadFlashcards()
    }

    private fun loadSession() {
        viewModelScope.launch {
            val session = sessionRepository.getSessionById(sessionId)
            _uiState.value = _uiState.value.copy(
                session = session,
                isLoading = false
            )
        }
    }

    private fun loadFlashcards() {
        viewModelScope.launch {
            getFlashcardsUseCase(sessionId).collect { flashcards ->
                _uiState.value = _uiState.value.copy(flashcards = flashcards)
            }
        }
    }

    fun endSession() {
        viewModelScope.launch {
            endSessionUseCase(sessionId)
        }
    }
}