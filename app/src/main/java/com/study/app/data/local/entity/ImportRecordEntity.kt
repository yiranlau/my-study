package com.study.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.study.app.domain.model.ImportRecord

@Entity(tableName = "import_records")
data class ImportRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fileName: String,
    val totalCount: Int,
    val successCount: Int,
    val failCount: Int,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = ImportRecord(
        id = id,
        fileName = fileName,
        totalCount = totalCount,
        successCount = successCount,
        failCount = failCount,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(record: ImportRecord) = ImportRecordEntity(
            id = record.id,
            fileName = record.fileName,
            totalCount = record.totalCount,
            successCount = record.successCount,
            failCount = record.failCount,
            createdAt = record.createdAt
        )
    }
}
