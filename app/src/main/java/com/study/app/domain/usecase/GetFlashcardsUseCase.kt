package com.study.app.domain.usecase

import com.study.app.domain.model.Flashcard
import com.study.app.domain.repository.FlashcardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFlashcardsUseCase @Inject constructor(
    private val repository: FlashcardRepository
) {
    operator fun invoke(sessionId: Long): Flow<List<Flashcard>> {
        return repository.getFlashcardsForSession(sessionId)
    }

    fun getAll(): Flow<List<Flashcard>> {
        return repository.getAllFlashcards()
    }
}