package com.study.app.ui.screens.child

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.study.app.domain.model.Grade
import com.study.app.domain.model.PracticeRecord
import com.study.app.domain.model.Subject
import com.study.app.ui.screens.result.ChildHistoryViewModel
import com.study.app.ui.theme.StudyPrimary
import com.study.app.util.Logger
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SubjectGradeSelectScreen(
    onNavigateToSettings: (Long, Long) -> Unit,
    onNavigateToRecordDetail: (Long) -> Unit = {},
    onNavigateToParent: () -> Unit = {},
    viewModel: SubjectGradeSelectViewModel = hiltViewModel(),
    historyViewModel: ChildHistoryViewModel = hiltViewModel()
) {
    val subjects by viewModel.subjects.collectAsState()
    val grades by viewModel.grades.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val selectedGrade by viewModel.selectedGrade.collectAsState()
    val practiceRecords by historyViewModel.practiceRecords.collectAsState()
    val statusBarsPadding = WindowInsets.statusBars.asPaddingValues()
    val context = LocalContext.current

    val credentialLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            onNavigateToParent()
        }
    }

    fun launchCredentialPrompt() {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (keyguardManager.isDeviceSecure) {
                val intent = keyguardManager.createConfirmDeviceCredentialIntent("家长验证", "请输入锁屏密码")
                if (intent != null) {
                    credentialLauncher.launch(intent)
                } else {
                    onNavigateToParent()
                }
            } else {
                onNavigateToParent()
            }
        }
    }

    val canProceed = selectedSubject != null && selectedGrade != null

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = statusBarsPadding.calculateTopPadding())
    ) {
        // Left side: Practice history
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "做题记录",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Summary card
                    HistorySummaryCard(practiceRecords = practiceRecords)

                    Spacer(modifier = Modifier.height(12.dp))

                    if (practiceRecords.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "暂无练习记录\n快去开始练习吧！",
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = practiceRecords.sortedByDescending { it.createdAt }.take(10),
                                key = { it.id }
                            ) { record ->
                                PracticeRecordItem(
                                    record = record,
                                    subjectName = historyViewModel.getSubjectName(record.subjectId) ?: "未知",
                                    gradeName = historyViewModel.getGradeName(record.gradeId) ?: "未知",
                                    accuracy = historyViewModel.getAccuracy(record),
                                    formattedDate = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                                        .format(Date(record.createdAt)),
                                    formattedDuration = historyViewModel.formatDuration(record.durationMillis),
                                    onClick = { onNavigateToRecordDetail(record.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Right side: Subject and Grade selection
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "学习App",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudyPrimary
                    )
                    IconButton(onClick = { launchCredentialPrompt() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "家长模式",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Subject section
                Text(
                    text = "选择学科",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(subjects, key = { it.id }) { subject ->
                        ChipItem(
                            text = subject.name,
                            isSelected = selectedSubject == subject,
                            onClick = { viewModel.selectSubject(subject) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Grade section
                Text(
                    text = "选择年级",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(grades, key = { it.id }) { grade ->
                        ChipItem(
                            text = grade.name,
                            isSelected = selectedGrade == grade,
                            onClick = { viewModel.selectGrade(grade) }
                        )
                    }
                }
            }

            // Big floating start button at bottom right
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = {
                        if (canProceed) {
                            val subjectId = selectedSubject?.id ?: 0L
                            val gradeId = selectedGrade?.id ?: 0L
                            onNavigateToSettings(subjectId, gradeId)
                        }
                    },
                    modifier = Modifier.size(72.dp),
                    containerColor = if (canProceed) StudyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "开始学习",
                        modifier = Modifier.size(36.dp),
                        tint = if (canProceed) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ChipItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) StudyPrimary else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HistorySummaryCard(practiceRecords: List<PracticeRecord>) {
    val totalCount = practiceRecords.size
    val totalQuestions = practiceRecords.sumOf { it.totalQuestions }
    val totalCorrect = practiceRecords.sumOf { it.correctCount }
    val overallAccuracy = if (totalQuestions > 0) {
        (totalCorrect.toFloat() / totalQuestions * 100).toInt()
    } else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = StudyPrimary.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$totalCount",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyPrimary
                )
                Text(
                    text = "练习次数",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$totalQuestions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyPrimary
                )
                Text(
                    text = "总题数",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${overallAccuracy}%",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyPrimary
                )
                Text(
                    text = "正确率",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PracticeRecordItem(
    record: PracticeRecord,
    subjectName: String,
    gradeName: String,
    accuracy: Float,
    formattedDate: String,
    formattedDuration: String,
    onClick: () -> Unit
) {
    val accuracyColor = when {
        accuracy >= 80 -> Color(0xFF4CAF50)
        accuracy >= 60 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$subjectName - $gradeName",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$formattedDate · ${record.totalQuestions}题 · $formattedDuration",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(accuracyColor.copy(alpha = 0.15f))
                    .border(1.dp, accuracyColor, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${accuracy.toInt()}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = accuracyColor
                )
            }
        }
    }
}
