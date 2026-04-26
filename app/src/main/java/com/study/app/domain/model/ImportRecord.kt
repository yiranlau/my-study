package com.study.app.domain.model

data class ImportRecord(
    val id: Long = 0,
    val fileName: String,
    val totalCount: Int,
    val successCount: Int,
    val failCount: Int,
    val createdAt: Long = System.currentTimeMillis()
)