package com.study.app.domain.usecase

import com.study.app.domain.model.Subject
import com.study.app.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubjectsUseCase @Inject constructor(
    private val repository: SubjectRepository
) {
    operator fun invoke(): Flow<List<Subject>> {
        return repository.getAll()
    }
}
