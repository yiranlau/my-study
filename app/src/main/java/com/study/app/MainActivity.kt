package com.study.app

import android.os.Bundle
import android.util.Log
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
        Log.d(TAG, "onCreate: start")
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: finished")
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
        Log.d(TAG, "onStart: start")
        super.onStart()
        Log.d(TAG, "onStart: finished")
    }

    override fun onResume() {
        Log.d(TAG, "onResume: start")
        super.onResume()
        Log.d(TAG, "onResume: finished")
    }

    override fun onPause() {
        Log.d(TAG, "onPause: start")
        super.onPause()
        Log.d(TAG, "onPause: finished")
    }

    override fun onStop() {
        Log.d(TAG, "onStop: start")
        super.onStop()
        Log.d(TAG, "onStop: finished")
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: start")
        super.onDestroy()
        Log.d(TAG, "onDestroy: finished")
    }

    override fun onRestart() {
        Log.d(TAG, "onRestart: start")
        super.onRestart()
        Log.d(TAG, "onRestart: finished")
    }
}
