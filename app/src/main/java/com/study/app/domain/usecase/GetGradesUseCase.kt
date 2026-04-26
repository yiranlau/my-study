package com.study.app.domain.usecase

import com.study.app.domain.model.Grade
import com.study.app.domain.repository.GradeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGradesUseCase @Inject constructor(
    private val repository: GradeRepository
) {
    operator fun invoke(): Flow<List<Grade>> {
        return repository.getAll()
    }
}
