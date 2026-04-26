package com.study.app.domain.model

data class Subject(
    val id: Long = 0,
    val name: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)