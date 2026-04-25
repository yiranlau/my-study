package com.study.app.data.repository

import com.study.app.data.local.FlashcardDao
import com.study.app.data.local.FlashcardEntity
import com.study.app.domain.model.Flashcard
import com.study.app.domain.repository.FlashcardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlashcardRepositoryImpl @Inject constructor(
    private val flashcardDao: FlashcardDao
) : FlashcardRepository {

    override fun getFlashcardsForSession(sessionId: Long): Flow<List<Flashcard>> {
        return flashcardDao.getFlashcardsForSession(sessionId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllFlashcards(): Flow<List<Flashcard>> {
        return flashcardDao.getAllFlashcards().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getFlashcardById(id: Long): Flashcard? {
        return flashcardDao.getFlashcardById(id)?.toDomain()
    }

    override suspend fun insertFlashcard(flashcard: Flashcard): Long {
        return flashcardDao.insertFlashcard(flashcard.toEntity())
    }

    override suspend fun updateFlashcard(flashcard: Flashcard) {
        flashcardDao.updateFlashcard(flashcard.toEntity())
    }

    override suspend fun deleteFlashcard(flashcard: Flashcard) {
        flashcardDao.deleteFlashcard(flashcard.toEntity())
    }

    private fun FlashcardEntity.toDomain(): Flashcard {
        return Flashcard(
            id = id,
            front = front,
            back = back,
            sessionId = sessionId,
            isLearned = isLearned
        )
    }

    private fun Flashcard.toEntity(): FlashcardEntity {
        return FlashcardEntity(
            id = id,
            front = front,
            back = back,
            sessionId = sessionId,
            isLearned = isLearned
        )
    }
}