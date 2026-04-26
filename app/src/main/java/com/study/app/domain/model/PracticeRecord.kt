package com.study.app.domain.model

data class PracticeRecord(
    val id: Long = 0,
    val subjectId: Long,
    val gradeId: Long,
    val totalQuestions: Int,
    val correctCount: Int,
    val durationMillis: Long,
    val questionResults: String,  // JSON array of per-question results
    val createdAt: Long = System.currentTimeMillis()
)