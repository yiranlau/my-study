package com.study.app.domain.model

data class WrongAnswerBook(
    val id: Long = 0,
    val questionId: Long,
    val studentAnswer: String,
    val correctAnswer: String,
    val addedAt: Long = System.currentTimeMillis()
)