package com.study.app.domain.repository

import com.study.app.domain.model.Flashcard
import kotlinx.coroutines.flow.Flow

interface FlashcardRepository {
    fun getFlashcardsForSession(sessionId: Long): Flow<List<Flashcard>>
    fun getAllFlashcards(): Flow<List<Flashcard>>
    suspend fun getFlashcardById(id: Long): Flashcard?
    suspend fun insertFlashcard(flashcard: Flashcard): Long
    suspend fun updateFlashcard(flashcard: Flashcard)
    suspend fun deleteFlashcard(flashcard: Flashcard)
}
