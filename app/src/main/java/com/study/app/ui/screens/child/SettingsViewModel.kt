package com.study.app.ui.screens.child

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _questionCount = MutableStateFlow(10)
    val questionCount: StateFlow<Int> = _questionCount

    private val _isTimeLimitEnabled = MutableStateFlow(false)
    val isTimeLimitEnabled: StateFlow<Boolean> = _isTimeLimitEnabled

    private val _timeLimitMinutes = MutableStateFlow(15)
    val timeLimitMinutes: StateFlow<Int> = _timeLimitMinutes

    fun setQuestionCount(count: Int) {
        _questionCount.value = count.coerceIn(1, 50)
    }

    fun setTimeLimitEnabled(enabled: Boolean) {
        _isTimeLimitEnabled.value = enabled
    }

    fun setTimeLimitMinutes(minutes: Int) {
        _timeLimitMinutes.value = minutes.coerceIn(1, 120)
    }
}
