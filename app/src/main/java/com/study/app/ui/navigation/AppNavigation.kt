package com.study.app.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.study.app.ui.screens.result.ChildHistoryScreen
import com.study.app.ui.screens.child.SettingsScreen
import com.study.app.ui.screens.child.SubjectGradeSelectScreen
import com.study.app.ui.screens.home.HomeScreen
import com.study.app.ui.screens.parent.ParentHomeScreen
import com.study.app.ui.screens.quiz.QuizScreen
import com.study.app.ui.screens.result.ResultScreen
import com.study.app.ui.screens.result.WrongBookScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object ParentHome : Screen("parent_home")
    data object SubjectGradeSelect : Screen("subject_grade_select")
    data object Settings : Screen("settings/{subjectId}/{gradeId}") {
        fun createRoute(subjectId: Long, gradeId: Long): String {
            return "settings/$subjectId/$gradeId"
        }
    }
    data object Quiz : Screen("quiz/{subjectId}/{gradeId}/{questionCount}/{timeLimitSeconds}") {
        fun createRoute(subjectId: Long, gradeId: Long, questionCount: Int, timeLimitSeconds: Int): String {
            return "quiz/$subjectId/$gradeId/$questionCount/$timeLimitSeconds"
        }
    }
    data object Result : Screen("result")
    data object WrongBook : Screen("wrong_book")
    data object ChildHistory : Screen("child_history")
}

private const val TAG = "NavAppNavigation"

@Composable
fun AppNavigation(navController: NavHostController) {
    Log.d(TAG, "AppNavigation: start")
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            Log.d(TAG, "Navigating to Home")
            HomeScreen(
                onNavigateToParent = {
                    Log.d(TAG, "Navigating to ParentHome")
                    navController.navigate(Screen.ParentHome.route)
                },
                onNavigateToChild = {
                    Log.d(TAG, "Navigating to SubjectGradeSelect")
                    navController.navigate(Screen.SubjectGradeSelect.route)
                }
            )
        }

        composable(Screen.ParentHome.route) {
            Log.d(TAG, "Navigating to ParentHome")
            ParentHomeScreen()
        }

        composable(Screen.SubjectGradeSelect.route) {
            Log.d(TAG, "Navigating to SubjectGradeSelect")
            SubjectGradeSelectScreen(
                onNavigateToSettings = { subjectId, gradeId ->
                    Log.d(TAG, "Navigating to Settings: subjectId=$subjectId, gradeId=$gradeId")
                    navController.navigate(Screen.Settings.createRoute(subjectId, gradeId))
                }
            )
        }

        composable(
            route = Screen.Settings.route,
            arguments = listOf(
                navArgument("subjectId") { type = androidx.navigation.NavType.LongType },
                navArgument("gradeId") { type = androidx.navigation.NavType.LongType }
            )
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: 0L
            val gradeId = backStackEntry.arguments?.getLong("gradeId") ?: 0L
            Log.d(TAG, "Navigating to Settings: subjectId=$subjectId, gradeId=$gradeId")

            SettingsScreen(
                subjectId = subjectId,
                gradeId = gradeId,
                onStartStudy = { sid, gid, questionCount, timeLimitSeconds ->
                    Log.d(TAG, "Navigating to Quiz: subjectId=$sid, gradeId=$gid, questionCount=$questionCount, timeLimitSeconds=$timeLimitSeconds")
                    navController.navigate(
                        Screen.Quiz.createRoute(sid, gid, questionCount, timeLimitSeconds)
                    )
                }
            )
        }

        composable(
            route = Screen.Quiz.route,
            arguments = listOf(
                navArgument("subjectId") { type = androidx.navigation.NavType.LongType },
                navArgument("gradeId") { type = androidx.navigation.NavType.LongType },
                navArgument("questionCount") { type = androidx.navigation.NavType.IntType },
                navArgument("timeLimitSeconds") { type = androidx.navigation.NavType.IntType }
            )
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: 0L
            val gradeId = backStackEntry.arguments?.getLong("gradeId") ?: 0L
            val questionCount = backStackEntry.arguments?.getInt("questionCount") ?: 10
            val timeLimitSeconds = backStackEntry.arguments?.getInt("timeLimitSeconds") ?: 0
            Log.d(TAG, "Navigating to Quiz: subjectId=$subjectId, gradeId=$gradeId, questionCount=$questionCount, timeLimitSeconds=$timeLimitSeconds")

            QuizScreen(
                subjectId = subjectId,
                gradeId = gradeId,
                questionCount = questionCount,
                timeLimitSeconds = timeLimitSeconds,
                onNavigateBack = { navController.popBackStack() },
                onQuizFinished = { _, _ ->
                    Log.d(TAG, "Navigating to Result (quiz finished)")
                    navController.navigate(Screen.Result.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(Screen.Result.route) {
            Log.d(TAG, "Navigating to Result")
            ResultScreen(
                onNavigateBack = { navController.popBackStack() },
                onRetry = { navController.popBackStack() }
            )
        }

        composable(Screen.WrongBook.route) {
            Log.d(TAG, "Navigating to WrongBook")
            WrongBookScreen(
                onNavigateBack = { navController.popBackStack() },
                onRetryWrongQuestions = {
                    Log.d(TAG, "Navigating to Quiz (retry wrong questions)")
                    navController.navigate(Screen.Quiz.createRoute(0L, 0L, 10, 0))
                }
            )
        }

        composable(Screen.ChildHistory.route) {
            Log.d(TAG, "Navigating to ChildHistory")
            ChildHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
    Log.d(TAG, "AppNavigation: finished")
}
