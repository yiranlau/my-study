package com.study.app.ui.screens.parent

import com.study.app.ui.screens.parent.NavItem
import com.study.app.ui.screens.parent.ParentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Test

class ParentViewModelTest {
    @Test
    fun initial_state_has_SUBJECTS_selected() {
        val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        try {
            val viewModel = ParentViewModel()

            assertEquals(NavItem.SUBJECTS, viewModel.selectedNavItem.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun selectNavItem_changes_selection() {
        val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        try {
            val viewModel = ParentViewModel()

            viewModel.selectNavItem(NavItem.GRADES)
            assertEquals(NavItem.GRADES, viewModel.selectedNavItem.value)

            viewModel.selectNavItem(NavItem.QUESTIONS)
            assertEquals(NavItem.QUESTIONS, viewModel.selectedNavItem.value)

            viewModel.selectNavItem(NavItem.IMPORT)
            assertEquals(NavItem.IMPORT, viewModel.selectedNavItem.value)

            viewModel.selectNavItem(NavItem.RECORDS)
            assertEquals(NavItem.RECORDS, viewModel.selectedNavItem.value)
        } finally {
            Dispatchers.resetMain()
        }
    }
}