package com.study.app.ui.screens.child

import com.study.app.util.Logger
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val TAG = "VMSettingsViewModel"

    private val _questionCount = MutableStateFlow(10)
    val questionCount: StateFlow<Int> = _questionCount

    private val _isTimeLimitEnabled = MutableStateFlow(false)
    val isTimeLimitEnabled: StateFlow<Boolean> = _isTimeLimitEnabled

    private val _timeLimitMinutes = MutableStateFlow(15)
    val timeLimitMinutes: StateFlow<Int> = _timeLimitMinutes

    fun setQuestionCount(count: Int) {
        val clampedCount = count.coerceIn(1, 50)
        Logger.d(TAG, "setQuestionCount: count=$count -> clamped=$clampedCount")
        _questionCount.value = clampedCount
    }

    fun setTimeLimitEnabled(enabled: Boolean) {
        Logger.d(TAG, "setTimeLimitEnabled: enabled=$enabled")
        _isTimeLimitEnabled.value = enabled
    }

    fun setTimeLimitMinutes(minutes: Int) {
        val clampedMinutes = minutes.coerceIn(1, 120)
        Logger.d(TAG, "setTimeLimitMinutes: minutes=$minutes -> clamped=$clampedMinutes")
        _timeLimitMinutes.value = clampedMinutes
    }
}
