package com.study.app.ui.screens.result

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.domain.model.Question
import com.study.app.domain.model.WrongAnswerBook
import com.study.app.domain.repository.WrongAnswerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResultUiState(
    val questionResults: List<QuestionResult> = emptyList(),
    val totalQuestions: Int = 0,
    val correctCount: Int = 0,
    val accuracy: Float = 0f,
    val durationMillis: Long = 0L,
    val isLoading: Boolean = false
)

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val wrongAnswerRepository: WrongAnswerRepository
) : ViewModel() {

    private val TAG = "VMResultViewModel"

    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    fun addToWrongBook(questionId: Long, userAnswer: String, correctAnswer: String) {
        Log.d(TAG, "addToWrongBook: questionId=$questionId, userAnswer=$userAnswer, correctAnswer=$correctAnswer")
        viewModelScope.launch {
            wrongAnswerRepository.insert(WrongAnswerBook(
                questionId = questionId,
                studentAnswer = userAnswer,
                correctAnswer = correctAnswer
            ))
            Log.d(TAG, "addToWrongBook: added to wrong book successfully")
        }
    }

    fun removeFromWrongBook(questionId: Long) {
        Log.d(TAG, "removeFromWrongBook: questionId=$questionId")
        viewModelScope.launch {
            wrongAnswerRepository.deleteByQuestionId(questionId)
            Log.d(TAG, "removeFromWrongBook: removed from wrong book successfully")
        }
    }

    fun calculateResults(
        questions: List<Question>,
        answers: Map<Int, String>,
        durationMillis: Long = 0L
    ) {
        Log.d(TAG, "calculateResults: ${questions.size} questions, ${answers.size} answers, duration=$durationMillis")
        val results = questions.mapIndexed { index, question ->
            QuestionResult(
                question = question,
                userAnswer = answers[index] ?: "",
                isCorrect = question.answer == answers[index],
                timeSpentMillis = 0L
            )
        }

        val correctCount = results.count { it.isCorrect }
        val totalQuestions = questions.size
        val accuracy = if (totalQuestions > 0) {
            correctCount.toFloat() / totalQuestions * 100
        } else 0f

        Log.d(TAG, "calculateResults: correct=$correctCount, total=$totalQuestions, accuracy=$accuracy%")
        _uiState.value = ResultUiState(
            questionResults = results,
            totalQuestions = totalQuestions,
            correctCount = correctCount,
            accuracy = accuracy,
            durationMillis = durationMillis,
            isLoading = false
        )
    }

    fun getAccuracy(correct: Int, total: Int): Float {
        return if (total > 0) correct.toFloat() / total * 100 else 0f
    }
}
