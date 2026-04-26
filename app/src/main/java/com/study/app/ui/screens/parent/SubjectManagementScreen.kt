package com.study.app.ui.screens.parent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.study.app.ui.components.SubjectCard

@Composable
fun SubjectManagementScreen(
    viewModel: SubjectManagementViewModel = hiltViewModel()
) {
    val subjects by viewModel.subjects.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var subjectToEdit by remember { mutableStateOf<com.study.app.domain.model.Subject?>(null) }
    var subjectToDelete by remember { mutableStateOf<com.study.app.domain.model.Subject?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (subjects.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无科目，点击 + 添加",
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
                items(subjects, key = { it.id }) { subject ->
                    SubjectCard(
                        subject = subject,
                        questionCount = 0,
                        onClick = { subjectToEdit = subject },
                        onLongClick = { subjectToDelete = subject }
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
            Icon(Icons.Default.Add, contentDescription = "添加科目")
        }
    }

    if (showAddDialog) {
        AddSubjectDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name ->
                viewModel.addSubject(name)
                showAddDialog = false
            }
        )
    }

    subjectToEdit?.let { subject ->
        EditSubjectDialog(
            subject = subject,
            onDismiss = { subjectToEdit = null },
            onConfirm = { updatedSubject ->
                viewModel.updateSubject(updatedSubject)
                subjectToEdit = null
            }
        )
    }

    subjectToDelete?.let { subject ->
        DeleteSubjectDialog(
            subject = subject,
            onDismiss = { subjectToDelete = null },
            onConfirm = {
                viewModel.deleteSubject(subject)
                subjectToDelete = null
            }
        )
    }
}

@Composable
private fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加科目") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("科目名称") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name) },
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
private fun EditSubjectDialog(
    subject: com.study.app.domain.model.Subject,
    onDismiss: () -> Unit,
    onConfirm: (com.study.app.domain.model.Subject) -> Unit
) {
    var name by remember { mutableStateOf(subject.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑科目") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("科目名称") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(subject.copy(name = name)) },
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
private fun DeleteSubjectDialog(
    subject: com.study.app.domain.model.Subject,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("删除科目") },
        text = { Text("确定要删除科目 \"${subject.name}\" 吗？") },
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
