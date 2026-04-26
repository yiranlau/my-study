package com.study.app.ui.screens.quiz

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.study.app.R
import com.study.app.domain.model.Question
import com.study.app.domain.model.QuestionType

@Composable
fun QuizScreen(
    subjectId: Long,
    gradeId: Long,
    questionCount: Int,
    timeLimitSeconds: Int,
    viewModel: QuizViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onQuizFinished: (Map<Int, String>, List<Question>) -> Unit = { _, _ -> }
) {
    val state by viewModel.state.collectAsState()

    // Load questions on first composition
    if (state.questions.isEmpty() && !state.isFinished) {
        viewModel.loadQuestions(subjectId, gradeId, questionCount)
        if (timeLimitSeconds > 0) {
            viewModel.setTimeLimit(timeLimitSeconds)
        }
    }

    Scaffold { paddingValues ->
        if (state.questions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("加载中...")
            }
        } else if (state.isFinished) {
            QuizFinishedContent(
                state = state,
                onNavigateBack = onNavigateBack
            )
        } else {
            QuizContent(
                state = state,
                currentQuestion = viewModel.currentQuestion,
                onAnswer = { viewModel.answerQuestion(it) },
                onNext = { viewModel.nextQuestion() },
                onPrevious = { viewModel.previousQuestion() },
                onNavigateBack = onNavigateBack,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun QuizContent(
    state: QuizState,
    currentQuestion: Question?,
    onAnswer: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (currentQuestion == null) return

    val isChoice = currentQuestion.type == QuestionType.CHOICE
    val themeColor = if (isChoice) {
        Color(0xFFFF9800) // Orange for CHOICE
    } else {
        Color(0xFF9C27B0) // Purple for FILL_BLANK
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Left: Question area
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            // Top bar with back button, progress and timer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                }

                // Progress indicator
                Text(
                    text = "${state.currentIndex + 1} / ${state.questions.size}",
                    style = MaterialTheme.typography.titleMedium
                )

                // Timer
                TimerDisplay(
                    remainingSeconds = state.remainingSeconds,
                    isTimeUp = state.isTimeUp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = (state.currentIndex + 1).toFloat() / state.questions.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = themeColor,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Question card
            QuestionCard(
                question = currentQuestion,
                themeColor = themeColor,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Answer section
            if (isChoice) {
                ChoiceAnswerSection(
                    options = parseOptions(currentQuestion.options),
                    selectedAnswer = state.answers[state.currentIndex],
                    themeColor = themeColor,
                    onAnswer = onAnswer
                )
            } else {
                FillBlankAnswerSection(
                    currentAnswer = state.answers[state.currentIndex] ?: "",
                    themeColor = themeColor,
                    onAnswer = onAnswer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation buttons
            NavigationButtons(
                canGoBack = state.currentIndex > 0,
                canGoForward = state.currentIndex < state.questions.size - 1,
                onPrevious = onPrevious,
                onNext = onNext
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Right: Progress indicator
        ProgressIndicator(
            totalQuestions = state.questions.size,
            currentIndex = state.currentIndex,
            answeredQuestions = state.answers.keys,
            modifier = Modifier.width(80.dp)
        )
    }
}

@Composable
private fun QuestionCard(
    question: Question,
    themeColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Type badge
            Box(
                modifier = Modifier
                    .background(themeColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (question.type == QuestionType.CHOICE) "选择题" else "填空题",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Question content
            Text(
                text = question.content,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ChoiceAnswerSection(
    options: List<String>,
    selectedAnswer: String?,
    themeColor: Color,
    onAnswer: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 2x2 grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.take(2).forEachIndexed { index, option ->
                OptionButton(
                    label = ('A' + index).toString(),
                    text = option,
                    isSelected = selectedAnswer == ('A' + index).toString(),
                    themeColor = themeColor,
                    onClick = { onAnswer(('A' + index).toString()) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.drop(2).forEachIndexed { index, option ->
                OptionButton(
                    label = ('C' + index).toString(),
                    text = option,
                    isSelected = selectedAnswer == ('C' + index).toString(),
                    themeColor = themeColor,
                    onClick = { onAnswer(('C' + index).toString()) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun OptionButton(
    label: String,
    text: String,
    isSelected: Boolean,
    themeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) themeColor else MaterialTheme.colorScheme.surface
    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(2.dp, themeColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FillBlankAnswerSection(
    currentAnswer: String,
    themeColor: Color,
    onAnswer: (String) -> Unit
) {
    var text by remember { mutableStateOf(currentAnswer) }

    Column {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onAnswer(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .border(2.dp, themeColor, RoundedCornerShape(8.dp))
                .padding(16.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (text.isEmpty()) {
                        Text(
                            text = "请输入答案...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
private fun TimerDisplay(
    remainingSeconds: Int,
    isTimeUp: Boolean
) {
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeColor = when {
        isTimeUp -> Color.Red
        remainingSeconds <= 30 -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Text(
        text = String.format("%02d:%02d", minutes, seconds),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = timeColor
    )
}

@Composable
private fun ProgressIndicator(
    totalQuestions: Int,
    currentIndex: Int,
    answeredQuestions: Set<Int>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxHeight(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed((1..totalQuestions).toList()) { index, num ->
                val isAnswered = answeredQuestions.contains(index)
                val isCurrent = index == currentIndex + 1

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCurrent -> Color.Red
                                isAnswered -> Color(0xFF4CAF50)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                        .border(
                            width = if (isCurrent) 3.dp else 1.dp,
                            color = if (isCurrent) Color.Red else MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = num.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isCurrent -> Color.White
                            isAnswered -> Color.White
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationButtons(
    canGoBack: Boolean,
    canGoForward: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onPrevious,
            enabled = canGoBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("上一题")
        }

        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White
            )
        ) {
            Text("下一题")
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
private fun QuizFinishedContent(
    state: QuizState,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (state.isTimeUp) "时间到！" else "答题完成！",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "已回答 ${state.answers.size} / ${state.questions.size} 题",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onNavigateBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Text("返回")
            }
        }
    }
}

private fun parseOptions(optionsJson: String?): List<String> {
    if (optionsJson.isNullOrBlank()) return emptyList()
    return try {
        // Simple JSON array parsing like ["A","B","C","D"]
        optionsJson
            .trim()
            .removePrefix("[")
            .removeSuffix("]")
            .split(",")
            .map { it.trim().removeSurrounding("\"") }
    } catch (e: Exception) {
        emptyList()
    }
}
