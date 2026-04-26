package com.study.app.ui.screens.result

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.study.app.domain.model.PracticeRecord
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.study.app.domain.model.QuestionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PracticeRecordDetailScreen(
    recordId: Long,
    onNavigateBack: () -> Unit,
    viewModel: PracticeRecordDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(recordId) {
        viewModel.loadRecord(recordId)
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "练习详情",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("加载中...", style = MaterialTheme.typography.bodyLarge)
                }
            } else if (uiState.record != null) {
                val record = uiState.record!!

                // Summary card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "${uiState.subjectName} - ${uiState.gradeName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                                .format(Date(record.createdAt)),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${record.totalQuestions}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "总题数",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${record.correctCount}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                                Text(
                                    text = "正确",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${record.totalQuestions - record.correctCount}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red
                                )
                                Text(
                                    text = "错误",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val accuracy = if (record.totalQuestions > 0)
                                    (record.correctCount.toFloat() / record.totalQuestions * 100).toInt()
                                else 0
                                Text(
                                    text = "${accuracy}%",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "正确率",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Questions list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(uiState.questionResults) { index, result ->
                        QuestionResultCard(
                            index = index + 1,
                            result = result
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("记录不存在")
                }
            }
        }
    }
}

@Composable
private fun QuestionResultCard(
    index: Int,
    result: QuestionResultDisplay
) {
    val backgroundColor = if (result.isCorrect) {
        Color(0xFF4CAF50).copy(alpha = 0.1f)
    } else {
        Color.Red.copy(alpha = 0.1f)
    }
    val borderColor = if (result.isCorrect) Color(0xFF4CAF50) else Color.Red

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (result.isCorrect) Color(0xFF4CAF50) else Color.Red),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (result.isCorrect) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "第${index}题",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            if (result.questionType == "CHOICE") Color(0xFFFF9800) else Color(0xFF9C27B0),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (result.questionType == "CHOICE") "选择题" else "填空题",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = result.questionContent,
                style = MaterialTheme.typography.bodyMedium
            )

            if (result.questionType == "CHOICE" && result.options != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "选项: ${result.options}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                Text(
                    text = "你的答案: ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = result.userAnswer.ifEmpty { "(未作答)" },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (result.isCorrect) Color(0xFF4CAF50) else Color.Red
                )
            }

            if (!result.isCorrect) {
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "正确答案: ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = result.correctAnswer,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}

data class QuestionResultDisplay(
    val questionId: Long,
    val questionContent: String,
    val questionType: String,
    val options: String?,
    val correctAnswer: String,
    val userAnswer: String,
    val isCorrect: Boolean
)

data class PracticeRecordDetailUiState(
    val isLoading: Boolean = true,
    val record: PracticeRecord? = null,
    val subjectName: String = "",
    val gradeName: String = "",
    val questionResults: List<QuestionResultDisplay> = emptyList()
)
