package com.study.app.domain.usecase

import com.study.app.domain.model.ImportRecord
import com.study.app.domain.model.Question
import com.study.app.domain.model.Subject
import com.study.app.domain.model.Grade
import com.study.app.domain.model.QuestionType
import com.study.app.domain.repository.ImportRepository
import com.study.app.domain.repository.QuestionRepository
import com.study.app.domain.repository.SubjectRepository
import com.study.app.domain.repository.GradeRepository
import com.study.app.data.import.CsvImporter
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ImportQuestionsUseCase @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val subjectRepository: SubjectRepository,
    private val gradeRepository: GradeRepository,
    private val importRepository: ImportRepository
) {
    private val csvImporter = CsvImporter()

    suspend operator fun invoke(csvContent: String, fileName: String): ImportResult {
        val lines = csvContent.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) {
            return ImportResult.Error("CSV file is empty")
        }

        var successCount = 0
        var errorCount = 0
        val errors = mutableListOf<String>()

        // Skip header line
        for (i in 1 until lines.size) {
            val result = csvImporter.parseLine(lines[i])
            when (result) {
                is com.study.app.data.import.CsvImportResult.Success -> {
                    // Resolve or create subject
                    val subject = subjectRepository.getByName(result.subjectName)
                        ?: subjectRepository.insert(Subject(name = result.subjectName))

                    // Resolve or create grade
                    val grade = gradeRepository.getByName(result.gradeName)
                        ?: gradeRepository.insert(Grade(name = result.gradeName))

                    // Insert question
                    val question = result.question.copy(
                        subjectId = if (subject is Long) subject else (subject as Subject).id,
                        gradeId = if (grade is Long) grade else (grade as Grade).id
                    )
                    questionRepository.insert(question)
                    successCount++
                }
                is com.study.app.data.import.CsvImportResult.Error -> {
                    errorCount++
                    errors.add("Line ${i + 1}: ${result.message}")
                }
            }
        }

        // Record import
        val importRecord = ImportRecord(
            fileName = fileName,
            totalCount = lines.size - 1,
            successCount = successCount,
            failCount = errorCount
        )
        importRepository.insert(importRecord)

        return if (errorCount > 0) {
            ImportResult.PartialSuccess(successCount, errorCount, errors)
        } else {
            ImportResult.Success(successCount)
        }
    }

    sealed class ImportResult {
        data class Success(val count: Int) : ImportResult()
        data class PartialSuccess(val successCount: Int, val errorCount: Int, val errors: List<String>) : ImportResult()
        data class Error(val message: String) : ImportResult()
    }
}
