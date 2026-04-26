package com.study.app.ui.screens.parent

import com.study.app.util.Logger
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

enum class NavItem {
    QUESTIONS,
    IMPORT,
    RECORDS
}

@HiltViewModel
class ParentViewModel @Inject constructor() : ViewModel() {
    private val TAG = "VMParentViewModel"

    private val _selectedNavItem = MutableStateFlow(NavItem.QUESTIONS)
    val selectedNavItem: StateFlow<NavItem> = _selectedNavItem.asStateFlow()

    fun selectNavItem(item: NavItem) {
        Logger.d(TAG, "selectNavItem: item=$item")
        _selectedNavItem.value = item
    }
}