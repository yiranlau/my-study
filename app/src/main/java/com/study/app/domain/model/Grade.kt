package com.study.app.domain.model

data class Grade(
    val id: Long = 0,
    val name: String,
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)