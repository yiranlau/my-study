package com.study.app.ui.screens.parent

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.study.app.ui.components.SideNavigation

@Composable
fun ParentHomeScreen(
    viewModel: ParentViewModel = hiltViewModel()
) {
    val selectedNavItem by viewModel.selectedNavItem.collectAsState()

    Row(modifier = Modifier.fillMaxSize()) {
        // Left side navigation
        SideNavigation(
            selectedNavItem = selectedNavItem,
            onNavItemSelected = { viewModel.selectNavItem(it) }
        )

        // Right side content area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = getContentTitle(selectedNavItem),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

private fun getContentTitle(item: NavItem): String {
    return when (item) {
        NavItem.SUBJECTS -> "科目管理"
        NavItem.GRADES -> "年级管理"
        NavItem.QUESTIONS -> "题目管理"
        NavItem.IMPORT -> "导入"
        NavItem.RECORDS -> "记录"
    }
}