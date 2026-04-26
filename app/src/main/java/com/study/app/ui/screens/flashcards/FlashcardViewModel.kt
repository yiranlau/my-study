package com.study.app.ui.screens.flashcards

import com.study.app.util.Logger
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.domain.model.Flashcard
import com.study.app.domain.repository.FlashcardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FlashcardUiState(
    val flashcards: List<Flashcard> = emptyList(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = true,
    val showAnswer: Boolean = false
)

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    private val repository: FlashcardRepository
) : ViewModel() {

    private val TAG = "VMFlashcardViewModel"

    private val _uiState = MutableStateFlow(FlashcardUiState())
    val uiState: StateFlow<FlashcardUiState> = _uiState.asStateFlow()

    init {
        Logger.d(TAG, "init: starting flashcard initialization")
        loadFlashcards()
    }

    private fun loadFlashcards() {
        viewModelScope.launch {
            Logger.d(TAG, "loadFlashcards: fetching all flashcards")
            repository.getAllFlashcards().collect { flashcards ->
                Logger.d(TAG, "loadFlashcards: received ${flashcards.size} flashcards, updating state")
                _uiState.value = _uiState.value.copy(
                    flashcards = flashcards,
                    isLoading = false
                )
            }
        }
    }

    fun toggleAnswer() {
        Logger.d(TAG, "toggleAnswer: showAnswer=${!_uiState.value.showAnswer}")
        _uiState.value = _uiState.value.copy(
            showAnswer = !_uiState.value.showAnswer
        )
    }

    fun nextCard() {
        val state = _uiState.value
        if (state.currentIndex < state.flashcards.size - 1) {
            Logger.d(TAG, "nextCard: currentIndex=${state.currentIndex} -> ${state.currentIndex + 1}")
            _uiState.value = state.copy(
                currentIndex = state.currentIndex + 1,
                showAnswer = false
            )
        } else {
            Logger.d(TAG, "nextCard: already at last card, no-op")
        }
    }

    fun previousCard() {
        val state = _uiState.value
        if (state.currentIndex > 0) {
            Logger.d(TAG, "previousCard: currentIndex=${state.currentIndex} -> ${state.currentIndex - 1}")
            _uiState.value = state.copy(
                currentIndex = state.currentIndex - 1,
                showAnswer = false
            )
        } else {
            Logger.d(TAG, "previousCard: already at first card, no-op")
        }
    }

    fun markAsLearned(flashcard: Flashcard) {
        Logger.d(TAG, "markAsLearned: flashcardId=${flashcard.id}")
        viewModelScope.launch {
            repository.updateFlashcard(flashcard.copy(isLearned = true))
            Logger.d(TAG, "markAsLearned: flashcard ${flashcard.id} marked as learned")
        }
    }
}