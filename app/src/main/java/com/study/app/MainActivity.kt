package com.study.app

import android.os.Bundle
import com.study.app.util.Logger
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.study.app.ui.navigation.AppNavigation
import com.study.app.ui.theme.StudyAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "NavMainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Logger.d(TAG, "onCreate: start")
        super.onCreate(savedInstanceState)
        Logger.d(TAG, "onCreate: finished")
        enableEdgeToEdge()
        setContent {
            StudyAppTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(navController = navController)
                }
            }
        }
    }

    override fun onStart() {
        Logger.d(TAG, "onStart: start")
        super.onStart()
        Logger.d(TAG, "onStart: finished")
    }

    override fun onResume() {
        Logger.d(TAG, "onResume: start")
        super.onResume()
        Logger.d(TAG, "onResume: finished")
    }

    override fun onPause() {
        Logger.d(TAG, "onPause: start")
        super.onPause()
        Logger.d(TAG, "onPause: finished")
    }

    override fun onStop() {
        Logger.d(TAG, "onStop: start")
        super.onStop()
        Logger.d(TAG, "onStop: finished")
    }

    override fun onDestroy() {
        Logger.d(TAG, "onDestroy: start")
        super.onDestroy()
        Logger.d(TAG, "onDestroy: finished")
    }

    override fun onRestart() {
        Logger.d(TAG, "onRestart: start")
        super.onRestart()
        Logger.d(TAG, "onRestart: finished")
    }
}
