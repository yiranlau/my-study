package com.study.app.ui.screens.child

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.study.app.domain.model.Grade
import com.study.app.domain.model.Subject
import com.study.app.ui.theme.StudyPrimary

@Composable
fun SubjectGradeSelectScreen(
    onNavigateToSettings: (Long, Long) -> Unit,
    viewModel: SubjectGradeSelectViewModel = hiltViewModel()
) {
    val subjects by viewModel.subjects.collectAsState()
    val grades by viewModel.grades.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val selectedGrade by viewModel.selectedGrade.collectAsState()

    val canProceed = selectedSubject != null && selectedGrade != null

    Row(modifier = Modifier.fillMaxSize()) {
        // Left side: Logo and title
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(StudyPrimary),
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
                    text = "选择学科和年级",
                    fontSize = 20.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Right side: Subject and Grade chips
        Column(
            modifier = Modifier
                .weight(1.5f)
                .fillMaxHeight()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Subject section
                Text(
                    text = "选择学科",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(subjects, key = { it.id }) { subject ->
                        ChipItem(
                            text = subject.name,
                            isSelected = selectedSubject == subject,
                            onClick = { viewModel.selectSubject(subject) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Grade section
                Text(
                    text = "选择年级",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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

            // Next button
            Button(
                onClick = {
                    val subjectId = selectedSubject?.id ?: 0L
                    val gradeId = selectedGrade?.id ?: 0L
                    onNavigateToSettings(subjectId, gradeId)
                },
                enabled = canProceed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "下一步",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
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
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) StudyPrimary else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}