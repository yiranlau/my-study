package com.study.app.ui.screens.quiz

import com.study.app.util.Logger
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.domain.model.Question
import com.study.app.domain.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val questionRepository: QuestionRepository
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
            val questions = questionRepository.getRandomQuestions(subjectId, gradeId, count)
            Logger.d(TAG, "loadQuestions: loaded ${questions.size} questions")
            _state.value = _state.value.copy(
                questions = questions,
                currentIndex = 0,
                answers = emptyMap(),
                isFinished = false,
                isTimeUp = false
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
        } else {
            _state.value = _state.value.copy(remainingSeconds = newSeconds)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        Logger.d(TAG, "onCleared: timer cancelled")
    }
}
