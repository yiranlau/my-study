package com.study.app.ui.screens.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.domain.model.Question
import com.study.app.domain.model.QuestionType
import com.study.app.domain.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class QuestionEntryViewModel @Inject constructor(
    private val questionRepository: QuestionRepository
) : ViewModel() {

    val selectedType = MutableStateFlow(QuestionType.CHOICE)
    val content = MutableStateFlow("")
    val options = MutableStateFlow(listOf("", "", "", ""))
    val answer = MutableStateFlow("")
    val hint = MutableStateFlow("")

    private val _subjectId = MutableStateFlow(0L)
    private val _gradeId = MutableStateFlow(0L)

    private val _subjectAndGrade = MutableStateFlow(Pair(0L, 0L))

    val questions: StateFlow<List<Question>> = _subjectAndGrade
        .flatMapLatest { (subjectId, gradeId) ->
            questionRepository.getBySubjectAndGrade(subjectId, gradeId)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setSubjectAndGrade(subjectId: Long, gradeId: Long) {
        _subjectAndGrade.value = Pair(subjectId, gradeId)
    }

    fun setType(type: QuestionType) {
        selectedType.value = type
    }

    fun updateContent(newContent: String) {
        content.value = newContent
    }

    fun updateOptions(newOptions: List<String>) {
        options.value = newOptions
    }

    fun updateAnswer(newAnswer: String) {
        answer.value = newAnswer
    }

    fun updateHint(newHint: String) {
        hint.value = newHint
    }

    fun saveQuestion() {
        viewModelScope.launch {
            val (subjectId, gradeId) = _subjectAndGrade.value
            val question = Question(
                subjectId = subjectId,
                gradeId = gradeId,
                type = selectedType.value,
                content = content.value,
                options = if (selectedType.value == QuestionType.CHOICE) options.value.joinToString(",") else null,
                answer = answer.value,
                hint = if (selectedType.value == QuestionType.FILL_BLANK) hint.value else null
            )
            questionRepository.insert(question)
            clearForm()
        }
    }

    private fun clearForm() {
        content.value = ""
        options.value = listOf("", "", "", "")
        answer.value = ""
        hint.value = ""
    }
}
