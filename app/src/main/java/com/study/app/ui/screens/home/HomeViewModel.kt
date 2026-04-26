package com.study.app.ui.screens.home

import com.study.app.util.Logger
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val TAG = "VMHomeViewModel"

    val isParentMode: Boolean
        get() = _isParentMode
    private var _isParentMode = false

    fun setParentMode(enabled: Boolean) {
        Logger.d(TAG, "setParentMode: enabled=$enabled")
        _isParentMode = enabled
    }

    fun exitParentMode() {
        Logger.d(TAG, "exitParentMode: exiting parent mode")
        _isParentMode = false
    }
}