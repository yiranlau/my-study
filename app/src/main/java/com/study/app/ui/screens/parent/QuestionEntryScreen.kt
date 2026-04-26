package com.study.app.ui.screens.parent

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val subjects by viewModel.subjects.collectAsState()
    val grades by viewModel.grades.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val selectedGrade by viewModel.selectedGrade.collectAsState()

    var subjectExpanded by remember { mutableStateOf(false) }
    var gradeExpanded by remember { mutableStateOf(false) }
    var showDuplicatesOnly by remember { mutableStateOf(false) }

    LaunchedEffect(subjectId, gradeId) {
        viewModel.setSubjectAndGrade(subjectId, gradeId)
    }

    // Get duplicate questions
    val duplicateQuestions = remember(questions) { viewModel.getDuplicateQuestions() }
    val displayedQuestions = if (showDuplicatesOnly) duplicateQuestions else questions

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

                // Subject and Grade selectors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Subject dropdown
                    ExposedDropdownMenuBox(
                        expanded = subjectExpanded,
                        onExpandedChange = { subjectExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedSubject?.name ?: "选择学科",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("学科") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = subjectExpanded,
                            onDismissRequest = { subjectExpanded = false }
                        ) {
                            subjects.forEach { subject ->
                                DropdownMenuItem(
                                    text = { Text(subject.name) },
                                    onClick = {
                                        viewModel.selectSubject(subject)
                                        subjectExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Grade dropdown
                    ExposedDropdownMenuBox(
                        expanded = gradeExpanded,
                        onExpandedChange = { gradeExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedGrade?.name ?: "选择年级",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("年级") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gradeExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = gradeExpanded,
                            onDismissRequest = { gradeExpanded = false }
                        ) {
                            grades.forEach { grade ->
                                DropdownMenuItem(
                                    text = { Text(grade.name) },
                                    onClick = {
                                        viewModel.selectGrade(grade)
                                        gradeExpanded = false
                                    }
                                )
                            }
                        }
                    }
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showDuplicatesOnly) "重复题目 (${displayedQuestions.size})" else "已录入题目 (${questions.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (duplicateQuestions.isNotEmpty()) {
                        FilterChip(
                            selected = showDuplicatesOnly,
                            onClick = { showDuplicatesOnly = !showDuplicatesOnly },
                            label = { Text("重复 (${duplicateQuestions.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Red.copy(alpha = 0.2f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (displayedQuestions.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (showDuplicatesOnly) "没有重复题目" else "暂无题目",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(displayedQuestions, key = { it.id }) { question ->
                            QuestionItemWithDelete(
                                question = question,
                                onDelete = { viewModel.deleteQuestion(question) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionItemWithDelete(
    question: Question,
    onDelete: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var isSwipedOpen by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        if (isSwipedOpen) Color.Red else if (offsetX < -50f) Color.Red else Color.Transparent,
        label = "background"
    )

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Background delete button
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(backgroundColor)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(
                onClick = {
                    onDelete()
                    offsetX = 0f
                    isSwipedOpen = false
                },
                enabled = isSwipedOpen || offsetX < -50f
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = Color.White
                )
            }
        }

        // Swipeable card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = if (isSwipedOpen) (-80f).dp else offsetX.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX < -80f) {
                                isSwipedOpen = true
                            } else {
                                offsetX = 0f
                                isSwipedOpen = false
                            }
                        },
                        onDragCancel = {
                            offsetX = 0f
                            isSwipedOpen = false
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            if (isSwipedOpen) {
                                // If already swiped open, allow swiping back to close
                                val newOffset = offsetX + dragAmount
                                if (newOffset >= 0f) {
                                    offsetX = 0f
                                    isSwipedOpen = false
                                } else {
                                    offsetX = newOffset.coerceIn(-200f, 0f)
                                }
                            } else {
                                offsetX = (offsetX + dragAmount).coerceIn(-200f, 0f)
                            }
                        }
                    )
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
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
    }
}
