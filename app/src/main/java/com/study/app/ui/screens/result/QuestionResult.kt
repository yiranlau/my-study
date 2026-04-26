package com.study.app.ui.screens.result

import com.study.app.domain.model.Question

data class QuestionResult(
    val question: Question,
    val userAnswer: String,
    val isCorrect: Boolean,
    val timeSpentMillis: Long
)
