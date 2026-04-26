package com.study.app.data.import

import com.study.app.util.Logger
import com.study.app.domain.model.Question
import com.study.app.domain.model.QuestionType

/**
 * Parser for CSV-formatted question data.
 *
 * CSV format:
 * type,subject,grade,content,options,answer
 * CHOICE,数学,一年级,1+1=?,["2","3","4","5"],A
 * FILL_BLANK,语文,二年级,中国的首都是?,,北京
 */
class CsvImporter {
    companion object {
        private const val TAG = "CSVImporter"
    }

    /**
     * Parses a single CSV line into a question.
     *
     * @param line CSV line to parse
     * @return CsvImportResult containing either a successful parse with question data, or an error
     */
    fun parseLine(line: String): CsvImportResult {
        Logger.d(TAG, "parseLine: parsing line content=${line.take(50)}...")
        return try {
            val parts = line.split(",")
            if (parts.size < 5) {
                Logger.e(TAG, "parseLine: invalid format at line, expected at least 5 fields, got ${parts.size}")
                return CsvImportResult.Error("Invalid format: expected at least 5 fields", line)
            }

            val type = when (parts[0].trim().uppercase()) {
                "CHOICE" -> QuestionType.CHOICE
                "FILL_BLANK" -> QuestionType.FILL_BLANK
                else -> {
                    Logger.e(TAG, "parseLine: unknown question type '${parts[0]}'")
                    return CsvImportResult.Error("Unknown type: ${parts[0]}", line)
                }
            }

            val subjectName = parts[1].trim()
            val gradeName = parts[2].trim()
            val content = parts[3].trim()

            // Options and answer parsing needs to handle JSON arrays with embedded commas
            // For CHOICE: options is JSON array like ["2","3","4","5"], answer is A/B/C/D
            // For FILL_BLANK: options is empty, answer is the text answer
            val options: String?
            val answer: String

            if (type == QuestionType.CHOICE) {
                // Find the JSON array start (look for part starting with '[')
                val jsonStartIndex = parts.indexOfFirst { it.trim().startsWith("[") }
                if (jsonStartIndex == -1) {
                    Logger.e(TAG, "parseLine: CHOICE question missing options JSON array")
                    return CsvImportResult.Error("CHOICE question requires options JSON array", line)
                }
                // Find the JSON array end (look for part ending with ']')
                val jsonEndIndex = parts.indexOfLast { it.trim().endsWith("]") }
                if (jsonEndIndex == -1 || jsonEndIndex < jsonStartIndex) {
                    Logger.e(TAG, "parseLine: invalid options JSON array format")
                    return CsvImportResult.Error("Invalid options JSON array format", line)
                }
                // Reconstruct the JSON array
                options = parts.subList(jsonStartIndex, jsonEndIndex + 1)
                    .joinToString(",")
                // Answer is the last part after the JSON array
                answer = if (jsonEndIndex + 1 < parts.size) parts[jsonEndIndex + 1].trim() else ""
            } else {
                // FILL_BLANK: options is parts[4] (empty), answer is parts[5]
                options = null
                answer = if (parts.size > 5) parts[5].trim() else ""
            }

            val question = Question(
                subjectId = 0,  // Will be resolved by caller
                gradeId = 0,    // Will be resolved by caller
                type = type,
                content = content,
                options = options,
                answer = answer
            )

            Logger.d(TAG, "parseLine: success, type=$type, subject=$subjectName, grade=$gradeName")
            CsvImportResult.Success(
                subjectName = subjectName,
                gradeName = gradeName,
                question = question
            )
        } catch (e: Exception) {
            Logger.e(TAG, "parseLine: error parsing line", e)
            CsvImportResult.Error(e.message ?: "Unknown error", line)
        }
    }
}