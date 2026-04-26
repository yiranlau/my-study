package com.study.app.ui.screens.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.data.import.CsvImportResult
import com.study.app.data.import.CsvImporter
import com.study.app.domain.model.Question
import com.study.app.domain.repository.GradeRepository
import com.study.app.domain.repository.QuestionRepository
import com.study.app.domain.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CsvImportViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val subjectRepository: SubjectRepository,
    private val gradeRepository: GradeRepository
) : ViewModel() {

    // CsvImporter is created directly to avoid Java keyword issue with package name 'data.import'
    private val csvImporter = CsvImporter()

    private val _importResults = MutableStateFlow<List<CsvImportResult>>(emptyList())
    val importResults: StateFlow<List<CsvImportResult>> = _importResults.asStateFlow()

    val successCount: Int
        get() = _importResults.value.filterIsInstance<CsvImportResult.Success>().size

    val errorCount: Int
        get() = _importResults.value.filterIsInstance<CsvImportResult.Error>().size

    fun parseFile(content: String) {
        val lines = content.lines().drop(1) // Skip header
        _importResults.value = lines.map { csvImporter.parseLine(it) }
    }

    fun confirmImport(subjectId: Long, gradeId: Long) {
        viewModelScope.launch {
            _importResults.value.filterIsInstance<CsvImportResult.Success>().forEach { result ->
                val question = result.question.copy(
                    subjectId = subjectId,
                    gradeId = gradeId
                )
                questionRepository.insert(question)
            }
        }
    }

    fun clearResults() {
        _importResults.value = emptyList()
    }

    // For testing only - allows direct state manipulation
    internal fun setImportResultsForTest(results: List<CsvImportResult>) {
        _importResults.value = results
    }
}
