package com.study.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.study.app.R
import com.study.app.ui.screens.flashcards.FlashcardScreen
import com.study.app.ui.screens.home.HomeScreen
import com.study.app.ui.screens.sessions.SessionScreen

sealed class Screen(val route: String, val labelRes: Int) {
    data object Home : Screen("home", R.string.home_title)
    data object Flashcards : Screen("flashcards", R.string.flashcards_title)
    data object Session : Screen("session/{sessionId}", R.string.sessions_title) {
        fun createRoute(sessionId: Long) = "session/$sessionId"
    }
}

private val bottomNavItems = listOf(Screen.Home, Screen.Flashcards)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (screen == Screen.Home) Icons.Default.Home else Icons.Default.School,
                                contentDescription = null
                            )
                        },
                        label = { Text(stringResource(screen.labelRes)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToSession = { sessionId ->
                        navController.navigate(Screen.Session.createRoute(sessionId))
                    }
                )
            }
            composable(Screen.Flashcards.route) {
                FlashcardScreen()
            }
            composable(Screen.Session.route) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId")?.toLongOrNull() ?: 0L
                SessionScreen(sessionId = sessionId)
            }
        }
    }
}