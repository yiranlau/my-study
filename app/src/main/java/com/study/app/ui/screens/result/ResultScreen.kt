package com.study.app.ui.screens.result

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.study.app.R
import com.study.app.domain.model.QuestionType

@Composable
fun ResultScreen(
    viewModel: ResultViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onRetry: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Left: Statistics card
            StatisticsCard(
                totalQuestions = uiState.totalQuestions,
                correctCount = uiState.correctCount,
                accuracy = uiState.accuracy,
                durationMillis = uiState.durationMillis,
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Right: Question results list
            QuestionResultsList(
                questionResults = uiState.questionResults,
                viewModel = viewModel,
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
            )
        }

        // Bottom buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onNavigateBack,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("返回")
            }

            Button(
                onClick = onRetry,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("重试")
            }
        }
    }
}

@Composable
private fun StatisticsCard(
    totalQuestions: Int,
    correctCount: Int,
    accuracy: Float,
    durationMillis: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "答题结果",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Accuracy ring chart
            AccuracyRingChart(
                accuracy = accuracy,
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Statistics
            StatRow(label = "总题数", value = totalQuestions.toString())
            Spacer(modifier = Modifier.height(8.dp))
            StatRow(label = "正确", value = correctCount.toString(), valueColor = Color(0xFF4CAF50))
            Spacer(modifier = Modifier.height(8.dp))
            StatRow(label = "错误", value = (totalQuestions - correctCount).toString(), valueColor = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))

            val minutes = (durationMillis / 1000 / 60).toInt()
            val seconds = ((durationMillis / 1000) % 60).toInt()
            StatRow(label = "用时", value = String.format("%02d:%02d", minutes, seconds))
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
private fun AccuracyRingChart(
    accuracy: Float,
    modifier: Modifier = Modifier
) {
    val sweepAngle = accuracy * 3.6f // 360 degrees * accuracy percentage
    val ringColor = when {
        accuracy >= 80 -> Color(0xFF4CAF50) // Green
        accuracy >= 60 -> Color(0xFFFF9800) // Orange
        else -> Color.Red
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Background ring
            drawArc(
                color = Color.LightGray.copy(alpha = 0.3f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
            )

            // Progress ring
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${accuracy.toInt()}%",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = ringColor
            )
            Text(
                text = "正确率",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuestionResultsList(
    questionResults: List<QuestionResult>,
    viewModel: ResultViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "题目详情",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (questionResults.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无题目结果",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(questionResults) { index, result ->
                        QuestionResultItem(
                            index = index + 1,
                            result = result,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionResultItem(
    index: Int,
    result: QuestionResult,
    viewModel: ResultViewModel
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
            .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status icon
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

                // Question type badge
                Box(
                    modifier = Modifier
                        .background(
                            if (result.question.type == QuestionType.CHOICE) Color(0xFFFF9800) else Color(0xFF9C27B0),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (result.question.type == QuestionType.CHOICE) "选择题" else "填空题",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Question content
            Text(
                text = result.question.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // User's answer
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

            // Correct answer (only show if wrong)
            if (!result.isCorrect) {
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "正确答案: ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = result.question.answer,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }

            // Add to wrong answer book button (only for wrong answers)
            if (!result.isCorrect) {
                Spacer(modifier = Modifier.height(8.dp))
                IconButton(
                    onClick = {
                        viewModel.addToWrongBook(
                            questionId = result.question.id,
                            userAnswer = result.userAnswer,
                            correctAnswer = result.question.answer
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "加入错题本",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
