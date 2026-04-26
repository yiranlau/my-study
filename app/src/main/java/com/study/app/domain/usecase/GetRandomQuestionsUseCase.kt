package com.study.app.domain.usecase

import com.study.app.domain.model.Question
import com.study.app.domain.repository.QuestionRepository
import javax.inject.Inject

class GetRandomQuestionsUseCase @Inject constructor(
    private val repository: QuestionRepository
) {
    suspend operator fun invoke(subjectId: Long, gradeId: Long, count: Int): List<Question> {
        return repository.getRandomQuestions(subjectId, gradeId, count)
    }
}
