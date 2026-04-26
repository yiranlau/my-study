package com.study.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.study.app.domain.model.WrongAnswerBook

@Entity(
    tableName = "wrong_answer_book",
    indices = [Index("questionId")]
)
data class WrongAnswerBookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questionId: Long,
    val studentAnswer: String,
    val correctAnswer: String,
    val addedAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = WrongAnswerBook(
        id = id,
        questionId = questionId,
        studentAnswer = studentAnswer,
        correctAnswer = correctAnswer,
        addedAt = addedAt
    )

    companion object {
        fun fromDomain(wrongAnswer: WrongAnswerBook) = WrongAnswerBookEntity(
            id = wrongAnswer.id,
            questionId = wrongAnswer.questionId,
            studentAnswer = wrongAnswer.studentAnswer,
            correctAnswer = wrongAnswer.correctAnswer,
            addedAt = wrongAnswer.addedAt
        )
    }
}
