package com.study.app.ui.screens.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.domain.repository.GradeRepository
import com.study.app.domain.repository.PracticeRepository
import com.study.app.domain.repository.SubjectRepository
import com.study.app.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class PracticeRecordDetailViewModel @Inject constructor(
    private val practiceRepository: PracticeRepository,
    private val subjectRepository: SubjectRepository,
    private val gradeRepository: GradeRepository
) : ViewModel() {

    private val TAG = "VMPracticeRecordDetail"
    private val _uiState = MutableStateFlow(PracticeRecordDetailUiState())
    val uiState: StateFlow<PracticeRecordDetailUiState> = _uiState.asStateFlow()

    fun loadRecord(recordId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val record = practiceRepository.getById(recordId)
            if (record == null) {
                Logger.d(TAG, "loadRecord: record not found, id=$recordId")
                _uiState.value = _uiState.value.copy(isLoading = false)
                return@launch
            }

            Logger.d(TAG, "loadRecord: record found, questionResults length=${record.questionResults.length}")
            Logger.d(TAG, "loadRecord: questionResults sample=${record.questionResults.take(200)}")

            val subjects = subjectRepository.getAll().first()
            val grades = gradeRepository.getAll().first()
            val subjectName = subjects.find { it.id == record.subjectId }?.name ?: "未知"
            val gradeName = grades.find { it.id == record.gradeId }?.name ?: "未知"

            val questionResults = parseQuestionResults(record.questionResults)
            Logger.d(TAG, "loadRecord: parsed ${questionResults.size} question results")

            _uiState.value = PracticeRecordDetailUiState(
                isLoading = false,
                record = record,
                subjectName = subjectName,
                gradeName = gradeName,
                questionResults = questionResults
            )
        }
    }

    private fun parseQuestionResults(json: String): List<QuestionResultDisplay> {
        if (json.isBlank() || json == "[]") {
            Logger.d(TAG, "parseQuestionResults: json is blank or empty")
            return emptyList()
        }

        return try {
            val jsonArray = JSONArray(json)
            (0 until jsonArray.length()).map { i ->
                val obj = jsonArray.getJSONObject(i)
                QuestionResultDisplay(
                    questionId = obj.optLong("questionId", 0),
                    questionContent = obj.optString("questionContent", ""),
                    questionType = obj.optString("questionType", "CHOICE"),
                    options = obj.optString("options").takeIf { it.isNotBlank() && it != "null" },
                    correctAnswer = obj.optString("correctAnswer", ""),
                    userAnswer = obj.optString("userAnswer", ""),
                    isCorrect = obj.optBoolean("isCorrect", false)
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
