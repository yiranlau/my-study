package com.study.app.ui.screens.sessions

import com.study.app.util.Logger
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

    private val TAG = "VMSessionViewModel"

    private val sessionId: Long = savedStateHandle.get<String>("sessionId")?.toLongOrNull() ?: 0L

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        Logger.d(TAG, "init: sessionId=$sessionId, loading session and flashcards")
        loadSession()
        loadFlashcards()
    }

    private fun loadSession() {
        viewModelScope.launch {
            Logger.d(TAG, "loadSession: fetching session id=$sessionId")
            val session = sessionRepository.getSessionById(sessionId)
            Logger.d(TAG, "loadSession: session loaded, isActive=${session?.isActive}")
            _uiState.value = _uiState.value.copy(
                session = session,
                isLoading = false
            )
        }
    }

    private fun loadFlashcards() {
        viewModelScope.launch {
            Logger.d(TAG, "loadFlashcards: fetching flashcards for session id=$sessionId")
            getFlashcardsUseCase(sessionId).collect { flashcards ->
                Logger.d(TAG, "loadFlashcards: received ${flashcards.size} flashcards")
                _uiState.value = _uiState.value.copy(flashcards = flashcards)
            }
        }
    }

    fun endSession() {
        Logger.d(TAG, "endSession: ending session id=$sessionId")
        viewModelScope.launch {
            endSessionUseCase(sessionId)
            Logger.d(TAG, "endSession: session ended successfully")
        }
    }
}