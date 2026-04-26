package com.study.app.ui.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.study.app.data.import.CsvImportResult
import com.study.app.domain.model.QuestionType

@Composable
fun CsvImportScreen(
    subjectId: Long,
    gradeId: Long,
    viewModel: CsvImportViewModel = hiltViewModel()
) {
    val importResults by viewModel.importResults.collectAsState()
    var csvContent by remember { mutableStateOf("") }
    var selectedSubjectId by remember { mutableLongStateOf(subjectId) }
    var selectedGradeId by remember { mutableLongStateOf(gradeId) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left side: Upload zone and CSV format reference
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
                    text = "CSV导入",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Upload zone
                UploadZone(
                    content = csvContent,
                    onContentChange = { content ->
                        csvContent = content
                        viewModel.parseFile(content)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Subject and Grade input
                OutlinedTextField(
                    value = selectedSubjectId.toString(),
                    onValueChange = { selectedSubjectId = it.toLongOrNull() ?: 0L },
                    label = { Text("科目ID") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = selectedGradeId.toString(),
                    onValueChange = { selectedGradeId = it.toLongOrNull() ?: 0L },
                    label = { Text("年级ID") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CSV Format reference table
                CsvFormatReference()

                Spacer(modifier = Modifier.weight(1f))

                // Download sample CSV button
                OutlinedButton(
                    onClick = { /* TODO: Implement sample CSV download */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("下载示例CSV")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Confirm import button
                Button(
                    onClick = { viewModel.confirmImport(selectedSubjectId, selectedGradeId) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = viewModel.successCount > 0
                ) {
                    Text("确认导入 (${viewModel.successCount}个成功, ${viewModel.errorCount}个失败)")
                }
            }
        }

        // Right side: Preview list
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
                    text = "导入预览",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Text(
                        text = "成功: ${viewModel.successCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "失败: ${viewModel.errorCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (importResults.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "请上传CSV文件预览导入结果",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = importResults) { result ->
                            ImportResultItem(result = result)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UploadZone(
    content: String,
    onContentChange: (String) -> Unit
) {
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isDragging) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                2.dp,
                if (isDragging) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(8.dp)
            )
            .clickable { /* File picker would be triggered here */ }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Upload,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "拖拽CSV文件到这里或点击选择",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "支持 .csv 文件",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    // Show content input when there's content
    if (content.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            label = { Text("CSV内容") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )
    }
}

@Composable
private fun CsvFormatReference() {
    Column {
        Text(
            text = "CSV格式说明",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "type,subject,grade,content,options,answer",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "CHOICE,数学,一年级,1+1=?,[\"2\",\"3\",\"4\",\"5\"],A",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "FILL_BLANK,语文,二年级,中国的首都是?,,北京",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "type: CHOICE (选择题) 或 FILL_BLANK (填空题)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "subject: 科目名称 (如: 数学, 语文)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "grade: 年级名称 (如: 一年级, 二年级)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "content: 题目内容",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "options: JSON数组格式的选择题选项 (填空题为空)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "answer: 答案 (选择题为A/B/C/D, 填空题为文本)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ImportResultItem(result: CsvImportResult) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = when (result) {
                is CsvImportResult.Success -> MaterialTheme.colorScheme.primaryContainer
                is CsvImportResult.Error -> MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (result) {
                    is CsvImportResult.Success -> Icons.Default.CheckCircle
                    is CsvImportResult.Error -> Icons.Default.Error
                },
                contentDescription = null,
                tint = when (result) {
                    is CsvImportResult.Success -> MaterialTheme.colorScheme.primary
                    is CsvImportResult.Error -> MaterialTheme.colorScheme.error
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                when (result) {
                    is CsvImportResult.Success -> {
                        Text(
                            text = if (result.question.type == QuestionType.CHOICE) "选择题" else "填空题",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = result.question.content,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2
                        )
                        Text(
                            text = "答案: ${result.question.answer}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    is CsvImportResult.Error -> {
                        Text(
                            text = "导入失败",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = result.message,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}
