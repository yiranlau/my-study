package com.study.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.study.app.domain.model.PracticeRecord

@Entity(
    tableName = "practice_records",
    indices = [
        Index("subjectId"),
        Index("gradeId")
    ]
)
data class PracticeRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    val gradeId: Long,
    val totalQuestions: Int,
    val correctCount: Int,
    val durationMillis: Long,
    val questionResults: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = PracticeRecord(
        id = id,
        subjectId = subjectId,
        gradeId = gradeId,
        totalQuestions = totalQuestions,
        correctCount = correctCount,
        durationMillis = durationMillis,
        questionResults = questionResults,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(record: PracticeRecord) = PracticeRecordEntity(
            id = record.id,
            subjectId = record.subjectId,
            gradeId = record.gradeId,
            totalQuestions = record.totalQuestions,
            correctCount = record.correctCount,
            durationMillis = record.durationMillis,
            questionResults = record.questionResults,
            createdAt = record.createdAt
        )
    }
}
