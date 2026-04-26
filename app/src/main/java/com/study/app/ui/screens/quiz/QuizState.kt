package com.study.app.ui.screens.quiz

import com.study.app.domain.model.Question

data class QuizState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val answers: Map<Int, String> = emptyMap(),
    val isFinished: Boolean = false,
    val remainingSeconds: Int = 0,
    val isTimeUp: Boolean = false
)
