package com.study.app.ui.screens.parent

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.study.app.ui.components.SideNavigation
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars

@Composable
fun ParentHomeScreen(
    viewModel: ParentViewModel = hiltViewModel()
) {
    val selectedNavItem by viewModel.selectedNavItem.collectAsState()
    val statusBarsPadding = WindowInsets.statusBars.asPaddingValues()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = statusBarsPadding.calculateTopPadding())
    ) {
        // Left side navigation
        SideNavigation(
            selectedNavItem = selectedNavItem,
            onNavItemSelected = { viewModel.selectNavItem(it) }
        )

        // Right side content area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (selectedNavItem) {
                NavItem.QUESTIONS -> QuestionEntryScreen(subjectId = 0L, gradeId = 0L)
                NavItem.IMPORT -> CsvImportScreen(subjectId = 0L, gradeId = 0L)
                NavItem.RECORDS -> ImportRecordsScreen()
            }
        }
    }
}