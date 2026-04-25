package com.study.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class FlashcardTest {
    @Test
    fun flashcard_has_front_and_back() {
        val card = Flashcard(
            id = 1L,
            front = "What is 2 + 2?",
            back = "4",
            sessionId = 1L
        )
        assertEquals("What is 2 + 2?", card.front)
        assertEquals("4", card.back)
    }
}
