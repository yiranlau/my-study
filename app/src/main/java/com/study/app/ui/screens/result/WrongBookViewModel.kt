package com.study.app.ui.screens.result

import com.study.app.util.Logger
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.data.local.GradeDao
import com.study.app.data.local.QuestionDao
import com.study.app.data.local.SubjectDao
import com.study.app.domain.model.WrongAnswerBook
import com.study.app.domain.repository.WrongAnswerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WrongBookViewModel @Inject constructor(
    private val wrongAnswerRepository: WrongAnswerRepository,
    private val questionDao: QuestionDao,
    private val subjectDao: SubjectDao,
    private val gradeDao: GradeDao
) : ViewModel() {

    private val TAG = "VMWrongBookViewModel"

    private val _selectedSubject = MutableStateFlow("全部")
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    private val allWrongQuestions = wrongAnswerRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val wrongQuestions: StateFlow<List<WrongQuestionItem>> = combine(
        allWrongQuestions,
        _selectedSubject
    ) { wrongAnswers, subject ->
        val items = wrongAnswers.mapNotNull { wrongAnswer ->
            val questionEntity = questionDao.getById(wrongAnswer.questionId) ?: return@mapNotNull null
            val question = questionEntity.toDomain()
            val subjectEntity = subjectDao.getById(question.subjectId)
            val gradeEntity = gradeDao.getById(question.gradeId)

            WrongQuestionItem(
                wrongAnswer = wrongAnswer,
                question = question,
                subjectName = subjectEntity?.name ?: "未知",
                gradeName = gradeEntity?.name ?: "未知"
            )
        }
        if (subject == "全部") items else items.filter { it.subjectName == subject }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun filterBySubject(subject: String) {
        _selectedSubject.value = subject
    }

    fun removeFromWrongBook(questionId: Long) {
        viewModelScope.launch {
            wrongAnswerRepository.deleteByQuestionId(questionId)
        }
    }
}
