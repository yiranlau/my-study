package com.study.app.domain.usecase

import com.study.app.domain.model.WrongAnswerBook
import com.study.app.domain.repository.WrongAnswerRepository
import javax.inject.Inject

class AddWrongAnswerUseCase @Inject constructor(
    private val repository: WrongAnswerRepository
) {
    suspend operator fun invoke(questionId: Long, studentAnswer: String, correctAnswer: String): Long {
        val wrongAnswer = WrongAnswerBook(
            questionId = questionId,
            studentAnswer = studentAnswer,
            correctAnswer = correctAnswer
        )
        return repository.insert(wrongAnswer)
    }
}
