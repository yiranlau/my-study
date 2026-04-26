package com.study.app.ui.screens.result

import com.study.app.domain.model.Question
import com.study.app.domain.model.WrongAnswerBook

data class WrongQuestionItem(
    val wrongAnswer: WrongAnswerBook,
    val question: Question,
    val subjectName: String,
    val gradeName: String
)
