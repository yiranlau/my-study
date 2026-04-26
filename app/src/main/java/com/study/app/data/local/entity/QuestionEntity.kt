package com.study.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.study.app.domain.model.Question
import com.study.app.domain.model.QuestionType

@Entity(
    tableName = "questions",
    indices = [
        Index("subjectId"),
        Index("gradeId")
    ]
)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long = 0,
    val gradeId: Long = 0,
    val type: QuestionType,
    val content: String,
    val options: String? = null,
    val answer: String,
    val hint: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = Question(
        id = id,
        subjectId = subjectId,
        gradeId = gradeId,
        type = type,
        content = content,
        options = options,
        answer = answer,
        hint = hint,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(question: Question) = QuestionEntity(
            id = question.id,
            subjectId = question.subjectId,
            gradeId = question.gradeId,
            type = question.type,
            content = question.content,
            options = question.options,
            answer = question.answer,
            hint = question.hint,
            createdAt = question.createdAt
        )
    }
}
