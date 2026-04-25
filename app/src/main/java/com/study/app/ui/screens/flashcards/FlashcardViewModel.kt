package com.study.app.ui.screens.flashcards

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

    private val _uiState = MutableStateFlow(FlashcardUiState())
    val uiState: StateFlow<FlashcardUiState> = _uiState.asStateFlow()

    init {
        loadFlashcards()
    }

    private fun loadFlashcards() {
        viewModelScope.launch {
            repository.getAllFlashcards().collect { flashcards ->
                _uiState.value = _uiState.value.copy(
                    flashcards = flashcards,
                    isLoading = false
                )
            }
        }
    }

    fun toggleAnswer() {
        _uiState.value = _uiState.value.copy(
            showAnswer = !_uiState.value.showAnswer
        )
    }

    fun nextCard() {
        val state = _uiState.value
        if (state.currentIndex < state.flashcards.size - 1) {
            _uiState.value = state.copy(
                currentIndex = state.currentIndex + 1,
                showAnswer = false
            )
        }
    }

    fun previousCard() {
        val state = _uiState.value
        if (state.currentIndex > 0) {
            _uiState.value = state.copy(
                currentIndex = state.currentIndex - 1,
                showAnswer = false
            )
        }
    }

    fun markAsLearned(flashcard: Flashcard) {
        viewModelScope.launch {
            repository.updateFlashcard(flashcard.copy(isLearned = true))
        }
    }
}