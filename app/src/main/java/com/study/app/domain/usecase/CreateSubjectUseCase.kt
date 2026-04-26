package com.study.app.domain.usecase

import com.study.app.domain.model.Subject
import com.study.app.domain.repository.SubjectRepository
import javax.inject.Inject

class CreateSubjectUseCase @Inject constructor(
    private val repository: SubjectRepository
) {
    suspend operator fun invoke(name: String): Long {
        val subject = Subject(name = name)
        return repository.insert(subject)
    }
}
