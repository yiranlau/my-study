package com.study.app.ui.screens.parent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.study.app.domain.model.Grade
import com.study.app.ui.components.GradeCard

@Composable
fun GradeManagementScreen(
    viewModel: GradeManagementViewModel = hiltViewModel()
) {
    val grades by viewModel.grades.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var gradeToEdit by remember { mutableStateOf<Grade?>(null) }
    var gradeToDelete by remember { mutableStateOf<Grade?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (grades.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无年级，点击 + 添加",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(grades, key = { it.id }) { grade ->
                    GradeCard(
                        grade = grade,
                        onClick = { gradeToEdit = grade },
                        onLongClick = { gradeToDelete = grade }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "添加年级")
        }
    }

    if (showAddDialog) {
        AddGradeDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, order ->
                viewModel.addGrade(name, order)
                showAddDialog = false
            }
        )
    }

    gradeToEdit?.let { grade ->
        EditGradeDialog(
            grade = grade,
            onDismiss = { gradeToEdit = null },
            onConfirm = { updatedGrade ->
                viewModel.updateGrade(updatedGrade)
                gradeToEdit = null
            }
        )
    }

    gradeToDelete?.let { grade ->
        DeleteGradeDialog(
            grade = grade,
            onDismiss = { gradeToDelete = null },
            onConfirm = {
                viewModel.deleteGrade(grade)
                gradeToDelete = null
            }
        )
    }
}

@Composable
private fun AddGradeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var order by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加年级") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("年级名称") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = order,
                    onValueChange = { order = it.filter { c -> c.isDigit() } },
                    label = { Text("顺序") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val orderInt = order.toIntOrNull() ?: 0
                    onConfirm(name, orderInt)
                },
                enabled = name.isNotBlank()
            ) {
                Text("添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
private fun EditGradeDialog(
    grade: Grade,
    onDismiss: () -> Unit,
    onConfirm: (Grade) -> Unit
) {
    var name by remember { mutableStateOf(grade.name) }
    var order by remember { mutableStateOf(grade.order.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑年级") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("年级名称") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = order,
                    onValueChange = { order = it.filter { c -> c.isDigit() } },
                    label = { Text("顺序") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val orderInt = order.toIntOrNull() ?: grade.order
                    onConfirm(grade.copy(name = name, order = orderInt))
                },
                enabled = name.isNotBlank()
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
private fun DeleteGradeDialog(
    grade: Grade,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("删除年级") },
        text = { Text("确定要删除年级 \"${grade.name}\" 吗？") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("删除")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
