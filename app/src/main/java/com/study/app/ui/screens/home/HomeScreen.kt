package com.study.app.ui.screens.home

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.study.app.R
import com.study.app.util.Logger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToParent: () -> Unit,
    onNavigateToChild: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var credentialError by remember { mutableStateOf(false) }

    val credentialLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            viewModel.setParentMode(true)
            onNavigateToParent()
            credentialError = false
        } else {
            credentialError = true
        }
    }

    fun launchCredentialPrompt() {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Use isDeviceSecure to check if lock screen is set (API 23+)
            if (keyguardManager.isDeviceSecure) {
                val intent = keyguardManager.createConfirmDeviceCredentialIntent(
                    "家长验证",
                    "请输入锁屏密码"
                )
                Logger.d("HomeScreen", "Device secure, launching credential intent: ${intent?.component}")
                if (intent != null) {
                    credentialLauncher.launch(intent)
                } else {
                    // Fallback: device claims secure but intent is null
                    Logger.d("HomeScreen", "Intent null despite device being secure")
                    viewModel.setParentMode(true)
                    onNavigateToParent()
                }
            } else {
                Logger.d("HomeScreen", "No lock screen set, allowing access")
                viewModel.setParentMode(true)
                onNavigateToParent()
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val intent = keyguardManager.createConfirmDeviceCredentialIntent(
                "家长验证",
                "请输入锁屏密码"
            )
            if (intent != null) {
                credentialLauncher.launch(intent)
            } else {
                viewModel.setParentMode(true)
                onNavigateToParent()
            }
        } else {
            viewModel.setParentMode(true)
            onNavigateToParent()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 家长入口 button (orange)
            Button(
                onClick = { launchCredentialPrompt() },
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "家长入口",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // 小孩入口 button (green)
            Button(
                onClick = onNavigateToChild,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "小孩入口",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}