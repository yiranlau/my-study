package com.study.app.ui.screens.child

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars

@Composable
fun SettingsScreen(
    subjectId: Long,
    gradeId: Long,
    onStartStudy: (Long, Long, Int, Int) -> Unit,
    onNavigateToHistory: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val questionCount by viewModel.questionCount.collectAsState()
    val isTimeLimitEnabled by viewModel.isTimeLimitEnabled.collectAsState()
    val timeLimitMinutes by viewModel.timeLimitMinutes.collectAsState()
    val statusBarsPadding = WindowInsets.statusBars.asPaddingValues()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = statusBarsPadding.calculateTopPadding())
    ) {
        // Left side: Logo and title
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "学习App",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "学习设置",
                    fontSize = 20.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // Right side: Settings card
        Column(
            modifier = Modifier
                .weight(1.5f)
                .fillMaxHeight()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Settings card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Question count slider
                    Text(
                        text = "题目数量",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Slider(
                            value = questionCount.toFloat(),
                            onValueChange = { viewModel.setQuestionCount(it.toInt()) },
                            valueRange = 1f..50f,
                            steps = 48,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "$questionCount",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Time limit switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "时间限制",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = isTimeLimitEnabled,
                            onCheckedChange = { viewModel.setTimeLimitEnabled(it) }
                        )
                    }

                    // Time limit minutes input (only visible when enabled)
                    if (isTimeLimitEnabled) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "限制时间（分钟）",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            OutlinedTextField(
                                value = timeLimitMinutes.toString(),
                                onValueChange = { value ->
                                    value.toIntOrNull()?.let { viewModel.setTimeLimitMinutes(it) }
                                },
                                modifier = Modifier.width(100.dp),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // View history button
            TextButton(
                onClick = onNavigateToHistory,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "查看练习历史",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Green gradient start button
            Button(
                onClick = {
                    val timeLimitSeconds = if (isTimeLimitEnabled) timeLimitMinutes * 60 else 0
                    onStartStudy(subjectId, gradeId, questionCount, timeLimitSeconds)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF7ED7C1),
                                    Color(0xFFA8E6CF)
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "开始学习",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
