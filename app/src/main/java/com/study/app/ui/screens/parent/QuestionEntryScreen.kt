package com.study.app.ui.screens.parent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.study.app.domain.model.Question
import com.study.app.domain.model.QuestionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionEntryScreen(
    subjectId: Long,
    gradeId: Long,
    viewModel: QuestionEntryViewModel = hiltViewModel()
) {
    val selectedType by viewModel.selectedType.collectAsState()
    val content by viewModel.content.collectAsState()
    val options by viewModel.options.collectAsState()
    val answer by viewModel.answer.collectAsState()
    val hint by viewModel.hint.collectAsState()
    val questions by viewModel.questions.collectAsState()

    LaunchedEffect(subjectId, gradeId) {
        viewModel.setSubjectAndGrade(subjectId, gradeId)
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left side: Question entry form
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Tab switching
                TabRow(
                    selectedTabIndex = if (selectedType == QuestionType.CHOICE) 0 else 1
                ) {
                    Tab(
                        selected = selectedType == QuestionType.CHOICE,
                        onClick = { viewModel.setType(QuestionType.CHOICE) },
                        text = { Text("选择题") }
                    )
                    Tab(
                        selected = selectedType == QuestionType.FILL_BLANK,
                        onClick = { viewModel.setType(QuestionType.FILL_BLANK) },
                        text = { Text("填空题") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content input
                OutlinedTextField(
                    value = content,
                    onValueChange = { viewModel.updateContent(it) },
                    label = { Text("题目内容") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedType == QuestionType.CHOICE) {
                    // CHOICE form: A/B/C/D options
                    val optionLabels = listOf("A", "B", "C", "D")
                    options.forEachIndexed { index, option ->
                        OutlinedTextField(
                            value = option,
                            onValueChange = { newValue ->
                                val newOptions = options.toMutableList()
                                newOptions[index] = newValue
                                viewModel.updateOptions(newOptions)
                            },
                            label = { Text("选项 ${optionLabels[index]}") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Answer selector
                    Text("选择答案:", style = MaterialTheme.typography.bodyMedium)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        optionLabels.forEach { label ->
                            FilterChip(
                                selected = answer == label,
                                onClick = { viewModel.updateAnswer(label) },
                                label = { Text(label) }
                            )
                        }
                    }
                } else {
                    // FILL_BLANK form
                    OutlinedTextField(
                        value = answer,
                        onValueChange = { viewModel.updateAnswer(it) },
                        label = { Text("答案") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = hint,
                        onValueChange = { viewModel.updateHint(it) },
                        label = { Text("提示 (可选)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("使用紫色主题", color = MaterialTheme.colorScheme.primary) }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { viewModel.saveQuestion() },
                    modifier = Modifier.align(Alignment.End),
                    enabled = content.isNotBlank() && answer.isNotBlank()
                ) {
                    Text("保存题目")
                }
            }
        }

        // Right side: List of entered questions
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "已录入题目 (${questions.size})",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (questions.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "暂无题目",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(questions, key = { it.id }) { question ->
                            QuestionItem(question = question)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionItem(question: Question) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (question.type == QuestionType.CHOICE) "选择题" else "填空题",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = question.answer,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = question.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
        }
    }
}
