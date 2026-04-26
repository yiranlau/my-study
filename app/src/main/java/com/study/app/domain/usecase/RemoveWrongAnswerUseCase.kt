package com.study.app.domain.usecase

import com.study.app.domain.repository.WrongAnswerRepository
import javax.inject.Inject

class RemoveWrongAnswerUseCase @Inject constructor(
    private val repository: WrongAnswerRepository
) {
    suspend operator fun invoke(questionId: Long) {
        repository.deleteByQuestionId(questionId)
    }
}
