package com.study.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.study.app.ui.screens.parent.NavItem

@Composable
fun SideNavigation(
    selectedNavItem: NavItem,
    onNavItemSelected: (NavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp)
        ) {
            // Logo area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "家长模式",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Navigation items
            NavItem.entries.forEach { item ->
                NavItemRow(
                    item = item,
                    isSelected = item == selectedNavItem,
                    onClick = { onNavItemSelected(item) }
                )
            }
        }
    }
}

@Composable
private fun NavItemRow(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.Transparent
    }

    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = getIconForNavItem(item),
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = getLabelForNavItem(item),
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = textColor
        )
    }
}

private fun getIconForNavItem(item: NavItem): String {
    return when (item) {
        NavItem.QUESTIONS -> "\u2753" // ❓
        NavItem.IMPORT -> "\uD83D\uDCE5" // 📥
        NavItem.RECORDS -> "\uD83D\uDCCB" // 📋
    }
}

private fun getLabelForNavItem(item: NavItem): String {
    return when (item) {
        NavItem.QUESTIONS -> "题目管理"
        NavItem.IMPORT -> "导入"
        NavItem.RECORDS -> "记录"
    }
}