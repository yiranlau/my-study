package com.study.app.ui.screens.quiz

import com.study.app.domain.model.PracticeRecord
import com.study.app.domain.model.Question
import com.study.app.domain.repository.PracticeRepository
import com.study.app.domain.repository.QuestionRepository
import com.study.app.util.Logger
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuestionResultJson(
    val questionId: Long,
    val questionContent: String,
    val questionType: String,
    val options: String?,
    val correctAnswer: String,
    val userAnswer: String,
    val isCorrect: Boolean
)

fun List<QuestionResultJson>.toJson(): String {
    val sb = StringBuilder()
    sb.append("[")
    for (i in indices) {
        val q = this[i]
        sb.append("{\"questionId\":${q.questionId},\"questionContent\":\"${q.questionContent.replace("\"", "\\\"")}\",\"questionType\":\"${q.questionType}\",\"options\":${if (q.options != null) "\"${q.options.replace("\"", "\\\"")}\"" else "null"},\"correctAnswer\":\"${q.correctAnswer}\",\"userAnswer\":\"${q.userAnswer}\",\"isCorrect\":${q.isCorrect}}")
        if (i < size - 1) sb.append(",")
    }
    sb.append("]")
    return sb.toString()
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val practiceRepository: PracticeRepository
) : ViewModel() {

    private val TAG = "VMQuizViewModel"

    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state.asStateFlow()

    private var timerJob: Job? = null

    val currentQuestion: Question?
        get() = _state.value.questions.getOrNull(_state.value.currentIndex)

    fun loadQuestions(subjectId: Long, gradeId: Long, count: Int) {
        Logger.d(TAG, "loadQuestions: subjectId=$subjectId, gradeId=$gradeId, count=$count")
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                subjectId = subjectId,
                gradeId = gradeId,
                startTimeMillis = System.currentTimeMillis()
            )
            val questions = questionRepository.getRandomQuestions(subjectId, gradeId, count)
            Logger.d(TAG, "loadQuestions: loaded ${questions.size} questions")
            _state.value = _state.value.copy(
                questions = questions,
                currentIndex = 0,
                answers = emptyMap(),
                isFinished = false,
                isTimeUp = false,
                isLoading = false
            )
        }
    }

    fun answerQuestion(answer: String) {
        val currentIndex = _state.value.currentIndex
        Logger.d(TAG, "answerQuestion: index=$currentIndex, answer=$answer")
        val newAnswers = _state.value.answers + (currentIndex to answer)
        _state.value = _state.value.copy(answers = newAnswers)
    }

    fun nextQuestion() {
        val nextIndex = _state.value.currentIndex + 1
        Logger.d(TAG, "nextQuestion: currentIndex=${_state.value.currentIndex} -> nextIndex=$nextIndex, total=${_state.value.questions.size}")
        if (nextIndex >= _state.value.questions.size) {
            Logger.d(TAG, "nextQuestion: finished quiz")
            _state.value = _state.value.copy(isFinished = true)
            savePracticeRecord()
        } else {
            _state.value = _state.value.copy(currentIndex = nextIndex)
        }
    }

    fun previousQuestion() {
        val prevIndex = _state.value.currentIndex - 1
        Logger.d(TAG, "previousQuestion: currentIndex=${_state.value.currentIndex} -> prevIndex=$prevIndex")
        if (prevIndex >= 0) {
            _state.value = _state.value.copy(currentIndex = prevIndex)
        } else {
            Logger.d(TAG, "previousQuestion: already at first question, no-op")
        }
    }

    fun setTimeLimit(seconds: Int) {
        Logger.d(TAG, "setTimeLimit: seconds=$seconds")
        _state.value = _state.value.copy(remainingSeconds = seconds)
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            Logger.d(TAG, "startTimer: timer started")
            while (_state.value.remainingSeconds > 0 && !_state.value.isTimeUp) {
                delay(1000)
                tickTimer()
            }
        }
    }

    fun tickTimer() {
        val newSeconds = _state.value.remainingSeconds - 1
        if (newSeconds <= 0) {
            Logger.d(TAG, "tickTimer: time is up!")
            _state.value = _state.value.copy(
                remainingSeconds = 0,
                isTimeUp = true,
                isFinished = true
            )
            savePracticeRecord()
        } else {
            _state.value = _state.value.copy(remainingSeconds = newSeconds)
        }
    }

    private fun savePracticeRecord() {
        val currentState = _state.value
        Logger.d(TAG, "savePracticeRecord: questions.size=${currentState.questions.size}, answers.size=${currentState.answers.size}")
        if (currentState.questions.isEmpty()) {
            Logger.d(TAG, "savePracticeRecord: early return - questions empty")
            return
        }

        Logger.d(TAG, "savePracticeRecord: mapping questions to results")
        val questionResultsList = currentState.questions.mapIndexed { index, question ->
            val userAnswer = currentState.answers[index] ?: ""
            val isCorrect = userAnswer == question.answer
            QuestionResultJson(
                questionId = question.id,
                questionContent = question.content,
                questionType = question.type.name,
                options = question.options,
                correctAnswer = question.answer,
                userAnswer = userAnswer,
                isCorrect = isCorrect
            )
        }
        val questionResultsJson = questionResultsList.toJson()
        Logger.d(TAG, "savePracticeRecord: questionResultsJson length=${questionResultsJson.length}, sample=${questionResultsJson.take(100)}")

        val correctCount = questionResultsList.count { it.isCorrect }
        val durationMillis = System.currentTimeMillis() - currentState.startTimeMillis

        Logger.d(TAG, "savePracticeRecord: subjectId=${currentState.subjectId}, gradeId=${currentState.gradeId}, correctCount=$correctCount, total=${currentState.questions.size}, duration=$durationMillis")

        viewModelScope.launch {
            val record = PracticeRecord(
                subjectId = currentState.subjectId,
                gradeId = currentState.gradeId,
                totalQuestions = currentState.questions.size,
                correctCount = correctCount,
                durationMillis = durationMillis,
                questionResults = questionResultsJson
            )
            practiceRepository.insert(record)
            Logger.d(TAG, "savePracticeRecord: saved successfully")
        }
    }

    fun finishQuiz() {
        if (!_state.value.isFinished) {
            _state.value = _state.value.copy(isFinished = true)
            savePracticeRecord()
        }
    }

    private fun List<Question>.countIndexed(predicate: (Int, Question) -> Boolean): Int {
        var count = 0
        forEachIndexed { index, question ->
            if (predicate(index, question)) count++
        }
        return count
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        Logger.d(TAG, "onCleared: timer cancelled")
    }
}
