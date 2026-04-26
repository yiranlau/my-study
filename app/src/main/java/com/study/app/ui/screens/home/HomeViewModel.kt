package com.study.app.ui.screens.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    val isParentMode: Boolean
        get() = _isParentMode
    private var _isParentMode = false

    fun setParentMode(enabled: Boolean) {
        _isParentMode = enabled
    }

    fun exitParentMode() {
        _isParentMode = false
    }
}