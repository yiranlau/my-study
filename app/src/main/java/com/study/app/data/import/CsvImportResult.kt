package com.study.app.data.import

/**
 * Represents the result of parsing a CSV line for question import.
 */
sealed class CsvImportResult {
    /**
     * Represents a successfully parsed question from CSV.
     */
    data class Success(
        val subjectName: String,
        val gradeName: String,
        val question: com.study.app.domain.model.Question
    ) : CsvImportResult()

    /**
     * Represents an error during CSV parsing.
     */
    data class Error(
        val message: String,
        val line: String
    ) : CsvImportResult()
}