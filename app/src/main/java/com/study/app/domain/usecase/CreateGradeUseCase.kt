package com.study.app.domain.usecase

import com.study.app.domain.model.Grade
import com.study.app.domain.repository.GradeRepository
import javax.inject.Inject

class CreateGradeUseCase @Inject constructor(
    private val repository: GradeRepository
) {
    suspend operator fun invoke(name: String): Long {
        val grade = Grade(name = name)
        return repository.insert(grade)
    }
}
