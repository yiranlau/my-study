package com.study.app.domain.model

data class StudySession(
    val id: Long = 0,
    val subject: String,
    val startTime: Long,
    val endTime: Long? = null,
    val durationMinutes: Int = 0,
    val isActive: Boolean = endTime == null
) {
    fun isCompleted(): Boolean = endTime != null
}
