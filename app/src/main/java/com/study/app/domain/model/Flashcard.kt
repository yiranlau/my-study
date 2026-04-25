package com.study.app.domain.model

data class Flashcard(
    val id: Long = 0,
    val front: String,
    val back: String,
    val sessionId: Long,
    val isLearned: Boolean = false
)
