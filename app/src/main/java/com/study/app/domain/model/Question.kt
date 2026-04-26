package com.study.app.domain.model

data class Question(
    val id: Long = 0,
    val subjectId: Long = 0,
    val gradeId: Long = 0,
    val type: QuestionType,
    val content: String,
    val options: String? = null,  // JSON array for CHOICE questions
    val answer: String,
    val hint: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)