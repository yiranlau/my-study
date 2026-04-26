package com.study.app.ui.screens.parent

import com.study.app.util.Logger
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.domain.model.Grade
import com.study.app.domain.model.Question
import com.study.app.domain.model.QuestionType
import com.study.app.domain.model.Subject
import com.study.app.domain.repository.GradeRepository
import com.study.app.domain.repository.QuestionRepository
import com.study.app.domain.repository.SubjectRepository
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
    private val questionRepository: QuestionRepository,
    private val subjectRepository: SubjectRepository,
    private val gradeRepository: GradeRepository
) : ViewModel() {

    private val TAG = "VMQuestionEntryViewModel"

    val selectedType = MutableStateFlow(QuestionType.CHOICE)
    val content = MutableStateFlow("")
    val options = MutableStateFlow(listOf("", "", "", ""))
    val answer = MutableStateFlow("")
    val hint = MutableStateFlow("")

    val subjects = subjectRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val grades = gradeRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _selectedSubject = MutableStateFlow<Subject?>(null)
    val selectedSubject: StateFlow<Subject?> = _selectedSubject

    private val _selectedGrade = MutableStateFlow<Grade?>(null)
    val selectedGrade: StateFlow<Grade?> = _selectedGrade

    private val _subjectAndGrade = MutableStateFlow(Pair(0L, 0L))

    val questions: StateFlow<List<Question>> = _subjectAndGrade
        .flatMapLatest { (subjectId, gradeId) ->
            questionRepository.getBySubjectAndGrade(subjectId, gradeId)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun selectSubject(subject: Subject) {
        Logger.d(TAG, "selectSubject: subject=${subject.name}")
        _selectedSubject.value = subject
        updateSubjectAndGrade()
    }

    fun selectGrade(grade: Grade) {
        Logger.d(TAG, "selectGrade: grade=${grade.name}")
        _selectedGrade.value = grade
        updateSubjectAndGrade()
    }

    private fun updateSubjectAndGrade() {
        val subjectId = _selectedSubject.value?.id ?: 0L
        val gradeId = _selectedGrade.value?.id ?: 0L
        _subjectAndGrade.value = Pair(subjectId, gradeId)
    }

    fun setSubjectAndGrade(subjectId: Long, gradeId: Long) {
        Logger.d(TAG, "setSubjectAndGrade: subjectId=$subjectId, gradeId=$gradeId")
        _subjectAndGrade.value = Pair(subjectId, gradeId)
    }

    fun setType(type: QuestionType) {
        Logger.d(TAG, "setType: type=$type")
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
        Logger.d(TAG, "saveQuestion: content=${content.value.take(50)}...")
        viewModelScope.launch {
            val subjectId = _selectedSubject.value?.id ?: 0L
            val gradeId = _selectedGrade.value?.id ?: 0L
            if (subjectId == 0L || gradeId == 0L) {
                Logger.w(TAG, "saveQuestion: subject or grade not selected")
                return@launch
            }
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
            Logger.d(TAG, "saveQuestion: question saved successfully, type=${selectedType.value}")
            clearForm()
        }
    }

    private fun clearForm() {
        Logger.d(TAG, "clearForm: clearing form")
        content.value = ""
        options.value = listOf("", "", "", "")
        answer.value = ""
        hint.value = ""
    }

    fun deleteQuestion(question: Question) {
        Logger.d(TAG, "deleteQuestion: questionId=${question.id}")
        viewModelScope.launch {
            questionRepository.delete(question)
            Logger.d(TAG, "deleteQuestion: question deleted")
        }
    }

    fun getDuplicateQuestions(): List<Question> {
        val currentQuestions = questions.value
        val seen = mutableSetOf<String>()
        val duplicates = mutableListOf<Question>()

        for (question in currentQuestions) {
            // Create a signature for deduplication: content + type + answer
            val signature = "${question.content}|${question.type.name}|${question.answer}"
            if (signature in seen) {
                duplicates.add(question)
            } else {
                seen.add(signature)
            }
        }
        return duplicates
    }
}
