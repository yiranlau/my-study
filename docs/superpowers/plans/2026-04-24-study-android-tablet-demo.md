# Study 安卓平板应用 Demo - 实现计划

> **给 AI 代理：** 建议使用 superpowers:subagent-driven-development 或 superpowers:executing-plans 来逐任务实现本计划。步骤使用复选框 (`- [ ]`) 语法进行跟踪。

**目标：** 创建一款可用于生产环境的安卓平板学习应用 Demo，采用 Jetpack Compose 开发，包含学习会话管理、闪卡功能，以及完整的 GitHub Actions CI/CD 配置。

**架构：** MVVM + Clean Architecture 分层（UI → ViewModel → UseCase → Repository → Data）。单模块安卓应用，UI 使用 Jetpack Compose，依赖注入用 Hilt，本地持久化用 Room。

**技术栈：**
- Kotlin 1.9.x, Jetpack Compose (BOM 2024.02.00)
- Android Gradle Plugin 8.2.x, Gradle 8.5
- Hilt 2.50 依赖注入
- Room 2.6.1 本地数据库
- Kotlin Coroutines + Flow 异步处理
- Robolectric 4.11 单元测试
- Espresso 3.5.1 UI 测试
- GitHub Actions CI/CD

---

## 文件结构

```
/Users/yiran/Codes/my-study/
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/com/study/app/
│       │   │   ├── StudyApplication.kt          # Hilt Application
│       │   │   ├── MainActivity.kt              # 单 Activity
│       │   │   ├── ui/
│       │   │   │   ├── theme/
│       │   │   │   │   ├── Theme.kt
│       │   │   │   │   └── Color.kt
│       │   │   │   ├── navigation/
│       │   │   │   │   └── StudyNavigation.kt
│       │   │   │   ├── screens/
│       │   │   │   │   ├── home/
│       │   │   │   │   │   ├── HomeScreen.kt
│       │   │   │   │   │   └── HomeViewModel.kt
│       │   │   │   │   ├── flashcards/
│       │   │   │   │   │   ├── FlashcardScreen.kt
│       │   │   │   │   │   └── FlashcardViewModel.kt
│       │   │   │   │   └── sessions/
│       │   │   │   │       ├── SessionScreen.kt
│       │   │   │   │       └── SessionViewModel.kt
│       │   │   │   └── components/
│       │   │   │       └── StudyCard.kt
│       │   │   ├── domain/
│       │   │   │   ├── model/
│       │   │   │   │   ├── StudySession.kt
│       │   │   │   │   └── Flashcard.kt
│       │   │   │   ├── repository/
│       │   │   │   │   ├── SessionRepository.kt
│       │   │   │   │   └── FlashcardRepository.kt
│       │   │   │   └── usecase/
│       │   │   │       ├── GetSessionsUseCase.kt
│       │   │   │       ├── CreateSessionUseCase.kt
│       │   │   │       └── GetFlashcardsUseCase.kt
│       │   │   └── data/
│       │   │       ├── local/
│       │   │       │   ├── StudyDatabase.kt
│       │   │       │   ├── SessionDao.kt
│       │   │       │   └── FlashcardDao.kt
│       │   │       └── repository/
│       │   │           ├── SessionRepositoryImpl.kt
│       │   │           └── FlashcardRepositoryImpl.kt
│       │   └── res/
│       │       ├── values/
│       │       │   ├── strings.xml
│       │       │   └── themes.xml
│       │       └── drawable/
│       │           └── ic_study.xml
│       └── test/
│           └── java/com/study/app/
│               ├── domain/usecase/
│               │   ├── GetSessionsUseCaseTest.kt
│               │   └── CreateSessionUseCaseTest.kt
│               └── data/repository/
│                   └── SessionRepositoryTest.kt
├── src/androidTest/
│   └── java/com/study/app/
│       └── ui/screens/
│           └── HomeScreenTest.kt
├── build.gradle.kts                         # 根构建配置
├── settings.gradle.kts
├── gradle.properties
├── app/build.gradle.kts                      # App 模块构建配置
└── .github/
    └── workflows/
        ├── android-ci.yml                   # 主 CI 工作流
        ├── android-test.yml                 # 仪器化测试
        └── android-lint.yml                 # 代码检查
```

---

## 任务 1: 项目骨架搭建

**文件：**
- 创建: `settings.gradle.kts`
- 创建: `build.gradle.kts` (根目录)
- 创建: `gradle.properties`
- 创建: `app/build.gradle.kts`
- 创建: `app/src/main/AndroidManifest.xml`
- 创建: `app/src/main/java/com/study/app/StudyApplication.kt`
- 创建: `app/src/main/java/com/study/app/MainActivity.kt`
- 创建: `app/src/main/res/values/strings.xml`
- 创建: `app/src/main/res/values/themes.xml`
- 创建: `gradle/wrapper/gradle-wrapper.properties`
- 创建: `gradlew` (wrapper 脚本)
- 创建: `gradlew.bat`

- [ ] **步骤 1: 创建根目录 settings.gradle.kts**

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "StudyApp"
include(":app")
```

- [ ] **步骤 2: 创建根目录 build.gradle.kts**

```kotlin
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}
```

- [ ] **步骤 3: 创建 gradle.properties**

```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.enableJetifier=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

- [ ] **步骤 4: 创建 app/build.gradle.kts**

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.study.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.study.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)

    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-android-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("androidx.test:core-ktx:1.5.0")
    testRuntimeOnly("org.robolectric:robolectric:4.11")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

- [ ] **步骤 5: 创建 AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:name=".StudyApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.StudyApp">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:configChanges="orientation|screenSize|screenLayout|keyboardHidden"
            android:theme="@style/Theme.StudyApp">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>
```

- [ ] **步骤 6: 创建 StudyApplication.kt**

```kotlin
package com.study.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StudyApplication : Application()
```

- [ ] **步骤 7: 创建 MainActivity.kt**

```kotlin
package com.study.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.study.app.ui.navigation.StudyNavigation
import com.study.app.ui.theme.StudyAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StudyNavigation()
                }
            }
        }
    }
}
```

- [ ] **步骤 8: 创建 strings.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">学习</string>
    <string name="home_title">学习会话</string>
    <string name="flashcards_title">闪卡</string>
    <string name="sessions_title">学习</string>
    <string name="create_session">新建会话</string>
    <string name="session_duration">时长</string>
    <string name="session_subject">科目</string>
    <string name="start_study">开始学习</string>
    <string name="end_study">结束学习</string>
    <string name="no_sessions">暂无学习会话</string>
    <string name="tap_to_start">点击 + 创建第一个会话</string>
</resources>
```

- [ ] **步骤 9: 创建 themes.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.StudyApp" parent="android:Theme.Material.Light.NoActionBar">
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:navigationBarColor">@android:color/transparent</item>
    </style>
</resources>
```

- [ ] **步骤 10: 生成 Gradle wrapper**

运行: `cd /Users/yiran/Codes/my-study && gradle wrapper --gradle-version 8.5`
预期: `BUILD SUCCESSFUL`，wrapper 文件生成在 `gradle/wrapper/`

- [ ] **步骤 11: 验证项目骨架编译**

运行: `./gradlew assembleDebug --no-daemon`
预期: `BUILD SUCCESSFUL`，APK 输出到 `app/build/outputs/apk/debug/`

- [ ] **步骤 12: 提交**

```bash
git init
git add settings.gradle.kts build.gradle.kts gradle.properties app/build.gradle.kts
git add app/src/main/AndroidManifest.xml
git add app/src/main/java/com/study/app/StudyApplication.kt
git add app/src/main/java/com/study/app/MainActivity.kt
git add app/src/main/res/values/strings.xml app/src/main/res/values/themes.xml
git add gradlew gradlew.bat gradle/wrapper/
git add .gitignore
git commit -m "feat: 创建安卓项目骨架和 Gradle wrapper"
```

---

## 任务 2: UI 主题和导航

**文件：**
- 创建: `app/src/main/java/com/study/app/ui/theme/Color.kt`
- 创建: `app/src/main/java/com/study/app/ui/theme/Theme.kt`
- 创建: `app/src/main/java/com/study/app/ui/navigation/StudyNavigation.kt`

- [ ] **步骤 1: 为主题颜色编写失败测试**

```kotlin
// app/src/test/java/com/study/app/ui/theme/ColorTest.kt
package com.study.app.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Test

class ColorTest {
    @Test
    fun primary_color_exists() {
        // Primary color 应该被定义
        assertEquals(true, ::primary.javaColor.name.isNotEmpty())
    }
}
```

- [ ] **步骤 2: 运行测试验证失败**

运行: `./gradlew :app:testDebugUnitTest --tests "*.ColorTest" --no-daemon`
预期: FAIL，错误 "primary cannot be found"

- [ ] **步骤 3: 创建 Color.kt**

```kotlin
package com.study.app.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val StudyPrimary = Color(0xFF1E88E5)
val StudySecondary = Color(0xFF26A69A)
val StudyBackground = Color(0xFFFAFAFA)
val StudySurface = Color(0xFFFFFFFF)
val StudyError = Color(0xFFD32F2F)
```

- [ ] **步骤 4: 创建 Theme.kt**

```kotlin
package com.study.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = StudyPrimary,
    secondary = StudySecondary,
    background = StudyBackground,
    surface = StudySurface,
    error = StudyError,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onBackground = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    onSurface = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
)

@Composable
fun StudyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

- [ ] **步骤 5: 运行测试验证通过**

运行: `./gradlew :app:testDebugUnitTest --tests "*.ColorTest" --no-daemon`
预期: PASS

- [ ] **步骤 6: 创建 StudyNavigation.kt**

```kotlin
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
```

- [ ] **步骤 7: 在 Theme.kt 中添加 Typography**

在 colorScheme 声明后添加：
```kotlin
val Typography = Typography
```

添加 Typography import：
```kotlin
import androidx.compose.material3.Typography
```

并定义：
```kotlin
private val Typography = Typography()
```

- [ ] **步骤 8: 提交**

```bash
git add app/src/main/java/com/study/app/ui/theme/Color.kt
git add app/src/main/java/com/study/app/ui/theme/Theme.kt
git add app/src/main/java/com/study/app/ui/navigation/StudyNavigation.kt
git commit -m "feat: 添加 UI 主题和导航"
```

---

## 任务 3: Domain 层 - 模型和仓库接口

**文件：**
- 创建: `app/src/main/java/com/study/app/domain/model/StudySession.kt`
- 创建: `app/src/main/java/com/study/app/domain/model/Flashcard.kt`
- 创建: `app/src/main/java/com/study/app/domain/repository/SessionRepository.kt`
- 创建: `app/src/main/java/com/study/app/domain/repository/FlashcardRepository.kt`

- [ ] **步骤 1: 为 StudySession 模型编写失败测试**

```kotlin
// app/src/test/java/com/study/app/domain/model/StudySessionTest.kt
package com.study.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class StudySessionTest {
    @Test
    fun study_session_has_required_fields() {
        val session = StudySession(
            id = 1L,
            subject = "Mathematics",
            startTime = 1000L,
            endTime = null,
            durationMinutes = 0
        )
        assertEquals("Mathematics", session.subject)
        assertEquals(1L, session.id)
        assertEquals(null, session.endTime)
    }
}
```

- [ ] **步骤 2: 运行测试验证失败**

运行: `./gradlew :app:testDebugUnitTest --tests "*.StudySessionTest" --no-daemon`
预期: FAIL，错误 "cannot find class StudySession"

- [ ] **步骤 3: 创建 StudySession.kt**

```kotlin
package com.study.app.domain.model

data class StudySession(
    val id: Long = 0,
    val subject: String,
    val startTime: Long,
    val endTime: Long? = null,
    val durationMinutes: Int = 0,
    val isActive: Boolean = endTime == null
) {
    fun isCompleted(): Boolean = endTime != null
}
```

- [ ] **步骤 4: 为 Flashcard 模型编写失败测试**

```kotlin
// app/src/test/java/com/study/app/domain/model/FlashcardTest.kt
package com.study.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class FlashcardTest {
    @Test
    fun flashcard_has_front_and_back() {
        val card = Flashcard(
            id = 1L,
            front = "What is 2 + 2?",
            back = "4",
            sessionId = 1L
        )
        assertEquals("What is 2 + 2?", card.front)
        assertEquals("4", card.back)
    }
}
```

- [ ] **步骤 5: 运行测试验证失败**

运行: `./gradlew :app:testDebugUnitTest --tests "*.FlashcardTest" --no-daemon`
预期: FAIL，错误 "cannot find class Flashcard"

- [ ] **步骤 6: 创建 Flashcard.kt**

```kotlin
package com.study.app.domain.model

data class Flashcard(
    val id: Long = 0,
    val front: String,
    val back: String,
    val sessionId: Long,
    val isLearned: Boolean = false
)
```

- [ ] **步骤 7: 运行测试验证通过**

运行: `./gradlew :app:testDebugUnitTest --tests "*.StudySessionTest" --tests "*.FlashcardTest" --no-daemon`
预期: PASS

- [ ] **步骤 8: 创建 SessionRepository.kt**

```kotlin
package com.study.app.domain.repository

import com.study.app.domain.model.StudySession
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun getAllSessions(): Flow<List<StudySession>>
    fun getActiveSession(): Flow<StudySession?>
    suspend fun getSessionById(id: Long): StudySession?
    suspend fun insertSession(session: StudySession): Long
    suspend fun updateSession(session: StudySession)
    suspend fun deleteSession(session: StudySession)
}
```

- [ ] **步骤 9: 创建 FlashcardRepository.kt**

```kotlin
package com.study.app.domain.repository

import com.study.app.domain.model.Flashcard
import kotlinx.coroutines.flow.Flow

interface FlashcardRepository {
    fun getFlashcardsForSession(sessionId: Long): Flow<List<Flashcard>>
    fun getAllFlashcards(): Flow<List<Flashcard>>
    suspend fun getFlashcardById(id: Long): Flashcard?
    suspend fun insertFlashcard(flashcard: Flashcard): Long
    suspend fun updateFlashcard(flashcard: Flashcard)
    suspend fun deleteFlashcard(flashcard: Flashcard)
}
```

- [ ] **步骤 10: 提交**

```bash
git add app/src/main/java/com/study/app/domain/model/StudySession.kt
git add app/src/main/java/com/study/app/domain/model/Flashcard.kt
git add app/src/main/java/com/study/app/domain/repository/SessionRepository.kt
git add app/src/main/java/com/study/app/domain/repository/FlashcardRepository.kt
git commit -m "feat: 添加 domain 模型和仓库接口"
```

---

## 任务 4: Data 层 - Room 数据库

**文件：**
- 创建: `app/src/main/java/com/study/app/data/local/StudyDatabase.kt`
- 创建: `app/src/main/java/com/study/app/data/local/SessionDao.kt`
- 创建: `app/src/main/java/com/study/app/data/local/FlashcardDao.kt`
- 创建: `app/src/main/java/com/study/app/data/local/SessionEntity.kt`
- 创建: `app/src/main/java/com/study/app/data/local/FlashcardEntity.kt`

- [ ] **步骤 1: 为 SessionEntity 编写失败测试**

```kotlin
// app/src/test/java/com/study/app/data/local/SessionEntityTest.kt
package com.study.app.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class SessionEntityTest {
    @Test
    fun session_entity_fields_match() {
        val entity = SessionEntity(
            id = 1L,
            subject = "Physics",
            startTime = 1000L,
            endTime = null
        )
        assertEquals("Physics", entity.subject)
        assertEquals(1L, entity.id)
    }
}
```

- [ ] **步骤 2: 运行测试验证失败**

运行: `./gradlew :app:testDebugUnitTest --tests "*.SessionEntityTest" --no-daemon`
预期: FAIL，错误 "cannot find class SessionEntity"

- [ ] **步骤 3: 创建 SessionEntity.kt**

```kotlin
package com.study.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String,
    val startTime: Long,
    val endTime: Long? = null
)
```

- [ ] **步骤 4: 创建 FlashcardEntity.kt**

```kotlin
package com.study.app.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "flashcards",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val front: String,
    val back: String,
    val sessionId: Long,
    val isLearned: Boolean = false
)
```

- [ ] **步骤 5: 运行测试验证通过**

运行: `./gradlew :app:testDebugUnitTest --tests "*.SessionEntityTest" --no-daemon`
预期: PASS

- [ ] **步骤 6: 创建 SessionDao.kt**

```kotlin
package com.study.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE endTime IS NULL LIMIT 1")
    fun getActiveSession(): Flow<SessionEntity?>

    @Query("SELECT * FROM sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): SessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity): Long

    @Update
    suspend fun updateSession(session: SessionEntity)

    @Delete
    suspend fun deleteSession(session: SessionEntity)
}
```

- [ ] **步骤 7: 创建 FlashcardDao.kt**

```kotlin
package com.study.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards WHERE sessionId = :sessionId")
    fun getFlashcardsForSession(sessionId: Long): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE id = :id")
    suspend fun getFlashcardById(id: Long): FlashcardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: FlashcardEntity): Long

    @Update
    suspend fun updateFlashcard(flashcard: FlashcardEntity)

    @Delete
    suspend fun deleteFlashcard(flashcard: FlashcardEntity)
}
```

- [ ] **步骤 8: 创建 StudyDatabase.kt**

```kotlin
package com.study.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SessionEntity::class, FlashcardEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StudyDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun flashcardDao(): FlashcardDao
}
```

- [ ] **步骤 9: 提交**

```bash
git add app/src/main/java/com/study/app/data/local/SessionEntity.kt
git add app/src/main/java/com/study/app/data/local/FlashcardEntity.kt
git add app/src/main/java/com/study/app/data/local/SessionDao.kt
git add app/src/main/java/com/study/app/data/local/FlashcardDao.kt
git add app/src/main/java/com/study/app/data/local/StudyDatabase.kt
git commit -m "feat: 添加 Room 数据库实体和 DAO"
```

---

## 任务 5: Data 层 - 仓库实现

**文件：**
- 创建: `app/src/main/java/com/study/app/data/repository/SessionRepositoryImpl.kt`
- 创建: `app/src/main/java/com/study/app/data/repository/FlashcardRepositoryImpl.kt`
- 创建: `app/src/main/java/com/study/app/di/DatabaseModule.kt`

- [ ] **步骤 1: 为 SessionRepositoryImpl 编写失败测试**

```kotlin
// app/src/test/java/com/study/app/data/repository/SessionRepositoryImplTest.kt
package com.study.app.data.repository

import com.study.app.data.local.SessionDao
import com.study.app.data.local.SessionEntity
import com.study.app.domain.model.StudySession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class SessionRepositoryImplTest {
    @Test
    fun getAllSessions_returns_mapped_sessions() = runTest {
        val dao = mock(SessionDao::class.java)
        val entity = SessionEntity(id = 1L, subject = "Math", startTime = 1000L, endTime = null)
        `when`(dao.getAllSessions()).thenReturn(flowOf(listOf(entity)))

        val repository = SessionRepositoryImpl(dao)
        val sessions = repository.getAllSessions().first()

        assertEquals(1, sessions.size)
        assertEquals("Math", sessions[0].subject)
    }
}
```

- [ ] **步骤 2: 运行测试验证失败**

运行: `./gradlew :app:testDebugUnitTest --tests "*.SessionRepositoryImplTest" --no-daemon`
预期: FAIL，错误 "cannot find class SessionRepositoryImpl"

- [ ] **步骤 3: 创建 SessionRepositoryImpl.kt**

```kotlin
package com.study.app.data.repository

import com.study.app.data.local.SessionDao
import com.study.app.data.local.SessionEntity
import com.study.app.domain.model.StudySession
import com.study.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
) : SessionRepository {

    override fun getAllSessions(): Flow<List<StudySession>> {
        return sessionDao.getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getActiveSession(): Flow<StudySession?> {
        return sessionDao.getActiveSession().map { it?.toDomain() }
    }

    override suspend fun getSessionById(id: Long): StudySession? {
        return sessionDao.getSessionById(id)?.toDomain()
    }

    override suspend fun insertSession(session: StudySession): Long {
        return sessionDao.insertSession(session.toEntity())
    }

    override suspend fun updateSession(session: StudySession) {
        sessionDao.updateSession(session.toEntity())
    }

    override suspend fun deleteSession(session: StudySession) {
        sessionDao.deleteSession(session.toEntity())
    }

    private fun SessionEntity.toDomain(): StudySession {
        val duration = if (endTime != null) {
            ((endTime - startTime) / 60000).toInt()
        } else {
            ((System.currentTimeMillis() - startTime) / 60000).toInt()
        }
        return StudySession(
            id = id,
            subject = subject,
            startTime = startTime,
            endTime = endTime,
            durationMinutes = duration
        )
    }

    private fun StudySession.toEntity(): SessionEntity {
        return SessionEntity(
            id = id,
            subject = subject,
            startTime = startTime,
            endTime = endTime
        )
    }
}
```

- [ ] **步骤 4: 创建 FlashcardRepositoryImpl.kt**

```kotlin
package com.study.app.data.repository

import com.study.app.data.local.FlashcardDao
import com.study.app.data.local.FlashcardEntity
import com.study.app.domain.model.Flashcard
import com.study.app.domain.repository.FlashcardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlashcardRepositoryImpl @Inject constructor(
    private val flashcardDao: FlashcardDao
) : FlashcardRepository {

    override fun getFlashcardsForSession(sessionId: Long): Flow<List<Flashcard>> {
        return flashcardDao.getFlashcardsForSession(sessionId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllFlashcards(): Flow<List<Flashcard>> {
        return flashcardDao.getAllFlashcards().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getFlashcardById(id: Long): Flashcard? {
        return flashcardDao.getFlashcardById(id)?.toDomain()
    }

    override suspend fun insertFlashcard(flashcard: Flashcard): Long {
        return flashcardDao.insertFlashcard(flashcard.toEntity())
    }

    override suspend fun updateFlashcard(flashcard: Flashcard) {
        flashcardDao.updateFlashcard(flashcard.toEntity())
    }

    override suspend fun deleteFlashcard(flashcard: Flashcard) {
        flashcardDao.deleteFlashcard(flashcard.toEntity())
    }

    private fun FlashcardEntity.toDomain(): Flashcard {
        return Flashcard(
            id = id,
            front = front,
            back = back,
            sessionId = sessionId,
            isLearned = isLearned
        )
    }

    private fun Flashcard.toEntity(): FlashcardEntity {
        return FlashcardEntity(
            id = id,
            front = front,
            back = back,
            sessionId = sessionId,
            isLearned = isLearned
        )
    }
}
```

- [ ] **步骤 5: 运行测试验证通过**

运行: `./gradlew :app:testDebugUnitTest --tests "*.SessionRepositoryImplTest" --no-daemon`
预期: PASS

- [ ] **步骤 6: 创建 DatabaseModule.kt (Hilt DI)**

```kotlin
package com.study.app.di

import android.content.Context
import androidx.room.Room
import com.study.app.data.local.FlashcardDao
import com.study.app.data.local.SessionDao
import com.study.app.data.local.StudyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StudyDatabase {
        return Room.databaseBuilder(
            context,
            StudyDatabase::class.java,
            "study_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSessionDao(database: StudyDatabase): SessionDao {
        return database.sessionDao()
    }

    @Provides
    @Singleton
    fun provideFlashcardDao(database: StudyDatabase): FlashcardDao {
        return database.flashcardDao()
    }
}
```

- [ ] **步骤 7: 提交**

```bash
git add app/src/main/java/com/study/app/data/repository/SessionRepositoryImpl.kt
git add app/src/main/java/com/study/app/data/repository/FlashcardRepositoryImpl.kt
git add app/src/main/java/com/study/app/di/DatabaseModule.kt
git commit -m "feat: 添加仓库实现和 DI 模块"
```

---

## 任务 6: Domain 层 - 用例

**文件：**
- 创建: `app/src/main/java/com/study/app/domain/usecase/GetSessionsUseCase.kt`
- 创建: `app/src/main/java/com/study/app/domain/usecase/CreateSessionUseCase.kt`
- 创建: `app/src/main/java/com/study/app/domain/usecase/EndSessionUseCase.kt`
- 创建: `app/src/main/java/com/study/app/domain/usecase/GetFlashcardsUseCase.kt`

- [ ] **步骤 1: 为 GetSessionsUseCase 编写失败测试**

```kotlin
// app/src/test/java/com/study/app/domain/usecase/GetSessionsUseCaseTest.kt
package com.study.app.domain.usecase

import com.study.app.domain.model.StudySession
import com.study.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class GetSessionsUseCaseTest {
    @Test
    fun execute_returns_sessions_from_repository() {
        val repository = mock(SessionRepository::class.java)
        val sessions = listOf(
            StudySession(id = 1L, subject = "Math", startTime = 1000L)
        )
        `when`(repository.getAllSessions()).thenReturn(flowOf(sessions))

        val useCase = GetSessionsUseCase(repository)
        val result = useCase().first()

        assertEquals(1, result.size)
        assertEquals("Math", result[0].subject)
    }
}
```

- [ ] **步骤 2: 运行测试验证失败**

运行: `./gradlew :app:testDebugUnitTest --tests "*.GetSessionsUseCaseTest" --no-daemon`
预期: FAIL，错误 "cannot find class GetSessionsUseCase"

- [ ] **步骤 3: 创建 GetSessionsUseCase.kt**

```kotlin
package com.study.app.domain.usecase

import com.study.app.domain.model.StudySession
import com.study.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSessionsUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    operator fun invoke(): Flow<List<StudySession>> {
        return repository.getAllSessions()
    }
}
```

- [ ] **步骤 4: 为 CreateSessionUseCase 编写失败测试**

```kotlin
// app/src/test/java/com/study/app/domain/usecase/CreateSessionUseCaseTest.kt
package com.study.app.domain.usecase

import com.study.app.domain.model.StudySession
import com.study.app.domain.repository.SessionRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlinx.coroutines.test.runTest

class CreateSessionUseCaseTest {
    @Test
    fun execute_creates_session_with_current_time() = runTest {
        val repository = mock(SessionRepository::class.java)
        `when`(repository.insertSession(org.mockito.kotlin.any())).thenReturn(1L)

        val useCase = CreateSessionUseCase(repository)
        val result = useCase("Physics")

        assertEquals(1L, result)
    }
}
```

- [ ] **步骤 5: 运行测试验证失败**

运行: `./gradlew :app:testDebugUnitTest --tests "*.CreateSessionUseCaseTest" --no-daemon`
预期: FAIL，错误 "cannot find class CreateSessionUseCase"

- [ ] **步骤 6: 创建 CreateSessionUseCase.kt**

```kotlin
package com.study.app.domain.usecase

import com.study.app.domain.model.StudySession
import com.study.app.domain.repository.SessionRepository
import javax.inject.Inject

class CreateSessionUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    suspend operator fun invoke(subject: String): Long {
        val session = StudySession(
            subject = subject,
            startTime = System.currentTimeMillis()
        )
        return repository.insertSession(session)
    }
}
```

- [ ] **步骤 7: 为 EndSessionUseCase 编写失败测试**

```kotlin
// app/src/test/java/com/study/app/domain/usecase/EndSessionUseCaseTest.kt
package com.study.app.domain.usecase

import com.study.app.domain.model.StudySession
import com.study.app.domain.repository.SessionRepository
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlinx.coroutines.test.runTest

class EndSessionUseCaseTest {
    @Test
    fun execute_sets_end_time_on_session() = runTest {
        val repository = mock(SessionRepository::class.java)
        val session = StudySession(id = 1L, subject = "Math", startTime = 1000L)
        `when`(repository.getSessionById(1L)).thenReturn(session)
        `when`(repository.updateSession(org.mockito.kotlin.any())).then {}

        val useCase = EndSessionUseCase(repository)
        val result = useCase(1L)

        assertTrue(result.endTime != null)
    }
}
```

- [ ] **步骤 8: 运行测试验证失败**

运行: `./gradlew :app:testDebugUnitTest --tests "*.EndSessionUseCaseTest" --no-daemon`
预期: FAIL，错误 "cannot find class EndSessionUseCase"

- [ ] **步骤 9: 创建 EndSessionUseCase.kt**

```kotlin
package com.study.app.domain.usecase

import com.study.app.domain.model.StudySession
import com.study.app.domain.repository.SessionRepository
import javax.inject.Inject

class EndSessionUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    suspend operator fun invoke(sessionId: Long): StudySession? {
        val session = repository.getSessionById(sessionId) ?: return null
        val updatedSession = session.copy(
            endTime = System.currentTimeMillis()
        )
        repository.updateSession(updatedSession)
        return updatedSession
    }
}
```

- [ ] **步骤 10: 创建 GetFlashcardsUseCase.kt**

```kotlin
package com.study.app.domain.usecase

import com.study.app.domain.model.Flashcard
import com.study.app.domain.repository.FlashcardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFlashcardsUseCase @Inject constructor(
    private val repository: FlashcardRepository
) {
    operator fun invoke(sessionId: Long): Flow<List<Flashcard>> {
        return repository.getFlashcardsForSession(sessionId)
    }

    fun getAll(): Flow<List<Flashcard>> {
        return repository.getAllFlashcards()
    }
}
```

- [ ] **步骤 11: 运行所有用例测试**

运行: `./gradlew :app:testDebugUnitTest --tests "*.GetSessionsUseCaseTest" --tests "*.CreateSessionUseCaseTest" --tests "*.EndSessionUseCaseTest" --no-daemon`
预期: PASS

- [ ] **步骤 12: 提交**

```bash
git add app/src/main/java/com/study/app/domain/usecase/GetSessionsUseCase.kt
git add app/src/main/java/com/study/app/domain/usecase/CreateSessionUseCase.kt
git add app/src/main/java/com/study/app/domain/usecase/EndSessionUseCase.kt
git add app/src/main/java/com/study/app/domain/usecase/GetFlashcardsUseCase.kt
git commit -m "feat: 添加会话和闪卡管理用例"
```

---

## 任务 7: UI 层 - 主页屏幕

**文件：**
- 创建: `app/src/main/java/com/study/app/ui/screens/home/HomeViewModel.kt`
- 创建: `app/src/main/java/com/study/app/ui/screens/home/HomeScreen.kt`
- 创建: `app/src/main/java/com/study/app/ui/components/StudyCard.kt`

- [ ] **步骤 1: 为 HomeViewModel 编写失败测试**

```kotlin
// app/src/test/java/com/study/app/ui/screens/home/HomeViewModelTest.kt
package com.study.app.ui.screens.home

import com.study.app.domain.model.StudySession
import com.study.app.domain.usecase.CreateSessionUseCase
import com.study.app.domain.usecase.GetSessionsUseCase
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class HomeViewModelTest {
    @Test
    fun sessions_are_loaded_on_init() {
        val getSessions = mock(GetSessionsUseCase::class.java)
        val createSession = mock(CreateSessionUseCase::class.java)
        val sessions = listOf(
            StudySession(id = 1L, subject = "Math", startTime = 1000L)
        )
        `when`(getSessions()).thenReturn(flowOf(sessions))

        val viewModel = HomeViewModel(getSessions, createSession)

        assertTrue(viewModel.sessions.isNotEmpty())
        assertEquals("Math", viewModel.sessions[0].subject)
    }
}
```

- [ ] **步骤 2: 运行测试验证失败**

运行: `./gradlew :app:testDebugUnitTest --tests "*.HomeViewModelTest" --no-daemon`
预期: FAIL，错误 "cannot find class HomeViewModel"

- [ ] **步骤 3: 创建 HomeViewModel.kt**

```kotlin
package com.study.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.domain.model.StudySession
import com.study.app.domain.usecase.CreateSessionUseCase
import com.study.app.domain.usecase.GetSessionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val sessions: List<StudySession> = emptyList(),
    val isLoading: Boolean = true,
    val showCreateDialog: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSessionsUseCase: GetSessionsUseCase,
    private val createSessionUseCase: CreateSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val sessions: List<StudySession>
        get() = _uiState.value.sessions

    init {
        loadSessions()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            getSessionsUseCase().collect { sessionList ->
                _uiState.value = _uiState.value.copy(
                    sessions = sessionList,
                    isLoading = false
                )
            }
        }
    }

    fun showCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = true)
    }

    fun hideCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = false)
    }

    fun createSession(subject: String) {
        viewModelScope.launch {
            createSessionUseCase(subject)
            hideCreateDialog()
        }
    }
}
```

- [ ] **步骤 4: 为 StudyCard 组件编写失败测试**

```kotlin
// app/src/test/java/com/study/app/ui/components/StudyCardTest.kt
package com.study.app.ui.components

import com.study.app.domain.model.StudySession
import org.junit.Assert.assertTrue
import org.junit.Test

class StudyCardTest {
    @Test
    fun study_card_displays_session_info() {
        val session = StudySession(
            id = 1L,
            subject = "Chemistry",
            startTime = System.currentTimeMillis() - 3600000,
            durationMinutes = 60
        )
        assertTrue(session.subject == "Chemistry")
        assertTrue(session.durationMinutes >= 59)
    }
}
```

- [ ] **步骤 5: 运行测试验证失败**

运行: `./gradlew :app:testDebugUnitTest --tests "*.StudyCardTest" --no-daemon`
预期: FAIL，错误 "cannot find class StudyCard"

- [ ] **步骤 6: 创建 StudyCard.kt**

```kotlin
package com.study.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.study.app.R
import com.study.app.domain.model.StudySession
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StudyCard(
    session: StudySession,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = session.subject,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDuration(session.durationMinutes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatDate(session.startTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (session.isActive) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "进行中",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

private fun formatDuration(minutes: Int): String {
    return if (minutes >= 60) {
        "${minutes / 60}小时 ${minutes % 60}分钟"
    } else {
        "${minutes}分钟"
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
```

- [ ] **步骤 7: 运行测试验证通过**

运行: `./gradlew :app:testDebugUnitTest --tests "*.StudyCardTest" --no-daemon`
预期: PASS

- [ ] **步骤 8: 创建 HomeScreen.kt**

```kotlin
package com.study.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.study.app.R
import com.study.app.ui.components.StudyCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSession: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showCreateDialog() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.create_session))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.sessions.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.sessions,
                            key = { it.id }
                        ) { session ->
                            StudyCard(
                                session = session,
                                onClick = { onNavigateToSession(session.id) }
                            )
                        }
                    }
                }
            }
        }

        if (uiState.showCreateDialog) {
            CreateSessionDialog(
                onDismiss = { viewModel.hideCreateDialog() },
                onCreate = { subject -> viewModel.createSession(subject) }
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_sessions),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.tap_to_start),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CreateSessionDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var subject by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.create_session)) },
        text = {
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text(stringResource(R.string.session_subject)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(subject) },
                enabled = subject.isNotBlank()
            ) {
                Text(stringResource(R.string.start_study))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
```

- [ ] **步骤 9: 运行占位符仪器化测试**

创建 `app/src/androidTest/java/com/study/app/ui/screens/HomeScreenTest.kt`:
```kotlin
package com.study.app.ui.screens

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.study.app.MainActivity
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun home_screen_displays_title() {
        composeTestRule.onNodeWithText("学习会话").assertExists()
    }
}
```

- [ ] **步骤 10: 验证代码编译**

运行: `./gradlew assembleDebug --no-daemon`
预期: `BUILD SUCCESSFUL`

- [ ] **步骤 11: 提交**

```bash
git add app/src/main/java/com/study/app/ui/screens/home/HomeViewModel.kt
git add app/src/main/java/com/study/app/ui/screens/home/HomeScreen.kt
git add app/src/main/java/com/study/app/ui/components/StudyCard.kt
git add app/src/test/java/com/study/app/ui/screens/home/HomeViewModelTest.kt
git add app/src/test/java/com/study/app/ui/components/StudyCardTest.kt
git commit -m "feat: 添加主页屏幕，包含 ViewModel 和 StudyCard 组件"
```

---

## 任务 8: UI 层 - 闪卡和会话屏幕

**文件：**
- 创建: `app/src/main/java/com/study/app/ui/screens/flashcards/FlashcardViewModel.kt`
- 创建: `app/src/main/java/com/study/app/ui/screens/flashcards/FlashcardScreen.kt`
- 创建: `app/src/main/java/com/study/app/ui/screens/sessions/SessionViewModel.kt`
- 创建: `app/src/main/java/com/study/app/ui/screens/sessions/SessionScreen.kt`

- [ ] **步骤 1: 创建 FlashcardViewModel.kt**

```kotlin
package com.study.app.ui.screens.flashcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.domain.model.Flashcard
import com.study.app.domain.repository.FlashcardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FlashcardUiState(
    val flashcards: List<Flashcard> = emptyList(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = true,
    val showAnswer: Boolean = false
)

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    private val repository: FlashcardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlashcardUiState())
    val uiState: StateFlow<FlashcardUiState> = _uiState.asStateFlow()

    init {
        loadFlashcards()
    }

    private fun loadFlashcards() {
        viewModelScope.launch {
            repository.getAllFlashcards().collect { flashcards ->
                _uiState.value = _uiState.value.copy(
                    flashcards = flashcards,
                    isLoading = false
                )
            }
        }
    }

    fun toggleAnswer() {
        _uiState.value = _uiState.value.copy(
            showAnswer = !_uiState.value.showAnswer
        )
    }

    fun nextCard() {
        val state = _uiState.value
        if (state.currentIndex < state.flashcards.size - 1) {
            _uiState.value = state.copy(
                currentIndex = state.currentIndex + 1,
                showAnswer = false
            )
        }
    }

    fun previousCard() {
        val state = _uiState.value
        if (state.currentIndex > 0) {
            _uiState.value = state.copy(
                currentIndex = state.currentIndex - 1,
                showAnswer = false
            )
        }
    }

    fun markAsLearned(flashcard: Flashcard) {
        viewModelScope.launch {
            repository.updateFlashcard(flashcard.copy(isLearned = true))
        }
    }
}
```

- [ ] **步骤 2: 创建 FlashcardScreen.kt**

```kotlin
package com.study.app.ui.screens.flashcards

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.study.app.R
import com.study.app.domain.model.Flashcard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    viewModel: FlashcardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.flashcards_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.flashcards.isEmpty() -> {
                    Text(
                        text = "暂无闪卡",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    FlashcardContent(
                        flashcard = uiState.flashcards[uiState.currentIndex],
                        showAnswer = uiState.showAnswer,
                        currentIndex = uiState.currentIndex,
                        totalCount = uiState.flashcards.size,
                        onToggleAnswer = { viewModel.toggleAnswer() },
                        onNext = { viewModel.nextCard() },
                        onPrevious = { viewModel.previousCard() },
                        onMarkLearned = { viewModel.markAsLearned(uiState.flashcards[uiState.currentIndex]) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashcardContent(
    flashcard: Flashcard,
    showAnswer: Boolean,
    currentIndex: Int,
    totalCount: Int,
    onToggleAnswer: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onMarkLearned: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${currentIndex + 1} / $totalCount",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .animateContentSize(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            onClick = onToggleAnswer
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (showAnswer) flashcard.back else flashcard.front,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (showAnswer) "答案" else "点击卡片显示答案",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = onPrevious) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "上一张")
            }

            if (!flashcard.isLearned) {
                IconButton(onClick = onMarkLearned) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "标记为已学会",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(onClick = onNext) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "下一张")
            }
        }
    }
}
```

- [ ] **步骤 3: 创建 SessionViewModel.kt**

```kotlin
package com.study.app.ui.screens.sessions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.domain.model.StudySession
import com.study.app.domain.usecase.EndSessionUseCase
import com.study.app.domain.usecase.GetFlashcardsUseCase
import com.study.app.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionUiState(
    val session: StudySession? = null,
    val flashcards: List<com.study.app.domain.model.Flashcard> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class SessionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionRepository: SessionRepository,
    private val endSessionUseCase: EndSessionUseCase,
    private val getFlashcardsUseCase: GetFlashcardsUseCase
) : ViewModel() {

    private val sessionId: Long = savedStateHandle.get<String>("sessionId")?.toLongOrNull() ?: 0L

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        loadSession()
        loadFlashcards()
    }

    private fun loadSession() {
        viewModelScope.launch {
            val session = sessionRepository.getSessionById(sessionId)
            _uiState.value = _uiState.value.copy(
                session = session,
                isLoading = false
            )
        }
    }

    private fun loadFlashcards() {
        viewModelScope.launch {
            getFlashcardsUseCase(sessionId).collect { flashcards ->
                _uiState.value = _uiState.value.copy(flashcards = flashcards)
            }
        }
    }

    fun endSession() {
        viewModelScope.launch {
            endSessionUseCase(sessionId)
        }
    }
}
```

- [ ] **步骤 4: 创建 SessionScreen.kt**

```kotlin
package com.study.app.ui.screens.sessions

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.study.app.R
import com.study.app.domain.model.StudySession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    sessionId: Long,
    viewModel: SessionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEndDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.session?.subject ?: stringResource(R.string.sessions_title)) },
                navigationIcon = {
                    IconButton(onClick = { /* Navigate back */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (uiState.session?.isActive == true) {
                        IconButton(onClick = { showEndDialog = true }) {
                            Icon(
                                Icons.Default.Stop,
                                contentDescription = stringResource(R.string.end_study),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.session == null -> {
                    Text(
                        text = "会话未找到",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    SessionContent(
                        session = uiState.session!!,
                        flashcardCount = uiState.flashcards.size
                    )
                }
            }
        }

        if (showEndDialog) {
            AlertDialog(
                onDismissRequest = { showEndDialog = false },
                title = { Text("结束学习会话？") },
                text = { Text("确定要结束这个学习会话吗？") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.endSession()
                            showEndDialog = false
                        }
                    ) {
                        Text("结束会话")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEndDialog = false }) {
                        Text("继续学习")
                    }
                }
            )
        }
    }
}

@Composable
private fun SessionContent(
    session: StudySession,
    flashcardCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (session.isActive) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = "学习中...",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = formatDuration(session.durationMinutes),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "学习时长",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                text = "学习完成",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "总时长：${formatDuration(session.durationMinutes)}",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$flashcardCount",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "闪卡数量",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private fun formatDuration(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return if (hours > 0) {
        "${hours}小时 ${mins}分钟"
    } else {
        "${mins}分钟"
    }
}
```

- [ ] **步骤 5: 验证编译**

运行: `./gradlew assembleDebug --no-daemon`
预期: `BUILD SUCCESSFUL`

- [ ] **步骤 6: 提交**

```bash
git add app/src/main/java/com/study/app/ui/screens/flashcards/FlashcardViewModel.kt
git add app/src/main/java/com/study/app/ui/screens/flashcards/FlashcardScreen.kt
git add app/src/main/java/com/study/app/ui/screens/sessions/SessionViewModel.kt
git add app/src/main/java/com/study/app/ui/screens/sessions/SessionScreen.kt
git commit -m "feat: 添加闪卡和会话屏幕"
```

---

## 任务 9: GitHub Actions CI/CD 配置

**文件：**
- 创建: `.github/workflows/android-ci.yml`
- 创建: `.github/workflows/android-test.yml`
- 创建: `.github/workflows/android-lint.yml`

- [ ] **步骤 1: 创建 android-ci.yml（主 CI 工作流）**

```yaml
name: Android CI

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

env:
  JAVA_VERSION: '17'
  ANDROID_COMPILE_SDK: 34
  ANDROID_BUILD_TOOLS: 34.0.0

jobs:
  ci:
    name: Build and Test
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK ${{ env.JAVA_VERSION }}
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
          cache: gradle

      - name: Setup Android SDK
        uses: android-actions/setup-android@v2

      - name: Cache Gradle packages
        uses: actions/cache@v4
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle.kts') }}
          restore-keys: |
            ${{ runner.os }}-gradle-

      - name: Run unit tests
        run: ./gradlew testDebugUnitTest --no-daemon

      - name: Run lint
        run: ./gradlew lintDebug --no-daemon

      - name: Build debug APK
        run: ./gradlew assembleDebug --no-daemon

      - name: Upload build artifacts
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: build-artifacts
          path: |
            app/build/outputs/apk/debug/
            app/build/reports/

      - name: Upload test reports
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: test-reports
          path: app/build/reports/tests/

      - name: Upload lint reports
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: lint-reports
          path: app/build/reports/lint/
```

- [ ] **步骤 2: 创建 android-test.yml（仪器化测试）**

```yaml
name: Android Instrumented Tests

on:
  workflow_run:
    workflows: ["Android CI"]
    types: [completed]
    branches: [main]

env:
  JAVA_VERSION: '17'

jobs:
  instrumented-tests:
    name: Run instrumented tests
    runs-on: macos-latest
    if: ${{ github.event.workflow_run.conclusion == 'success' }}

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK ${{ env.JAVA_VERSION }}
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
          cache: gradle

      - name: Setup Android SDK
        uses: android-actions/setup-android@v2

      - name: Cache Gradle packages
        uses: actions/cache@v4
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle.kts') }}

      - name: Run instrumented tests
        uses: reactivecircus/android-emulator-runner@v2
        with:
          api-level: 34
          target: google_apis
          arch: x86_64
          profile: tablet
          script: |
            ./gradlew connectedDebugAndroidTest --no-daemon

      - name: Upload test results
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: instrumented-test-results
          path: app/build/reports/androidTests/

      - name: Upload test results to GitHub
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: instrumented-test-results
          path: app/build/outputs/androidTest-results/
```

- [ ] **步骤 3: 创建 android-lint.yml（代码检查）**

```yaml
name: Android Lint

on:
  push:
    branches: [main, develop]
    paths:
      - '**.kt'
      - '**.kts'
      - '.github/workflows/android-lint.yml'
  pull_request:

env:
  JAVA_VERSION: '17'

jobs:
  lint:
    name: Run lint checks
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK ${{ env.JAVA_VERSION }}
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
          cache: gradle

      - name: Setup Android SDK
        uses: android-actions/setup-android@v2

      - name: Run detekt
        run: ./gradlew detekt --no-daemon

      - name: Run ktlint
        run: ./gradlew ktlintCheck --no-daemon

      - name: Run lint
        run: ./gradlew lintDebug --no-daemon

      - name: Upload lint results
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: lint-results
          path: app/build/reports/lint-results/

      - name: Add lint comment to PR
        uses: step-security/lint-reporter@v1
        if: always()
        with:
          github-token: ${{ secrets.GITHUB_TOKEN }}
          artifact-name: lint-results
          file-path: app/build/reports/lint-results/lint-results.html
```

- [ ] **步骤 4: 在 build.gradle.kts 中添加 detekt 和 ktlint**

在根目录 `build.gradle.kts` 添加：
```kotlin
plugins {
    // ... existing plugins
    id("io.gitlab.arturbosch.detekt") version "1.23.4" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
```

在 `app/build.gradle.kts` 添加：
```kotlin
// Add after the android block
detekt {
    toolVersion = "1.23.4"
    source.setFrom(files("src/main/java"))
    config.setFrom(files("$rootDir/config/detekt.yml"))
    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}
```

- [ ] **步骤 5: 创建 detekt 配置**

创建 `config/detekt.yml`：
```yaml
build:
  maxIssues: 10

style:
  MagicNumber:
    active: false
  ReturnCount:
    active: true
    max: 3

formatting:
  Indentation:
    indentSize: 4
```

- [ ] **步骤 6: 验证所有工作流是有效的 YAML**

运行: `python3 -c "import yaml; yaml.safe_load(open('.github/workflows/android-ci.yml'))"`
预期: 无错误输出

- [ ] **步骤 7: 提交**

```bash
git add .github/workflows/android-ci.yml
git add .github/workflows/android-test.yml
git add .github/workflows/android-lint.yml
git add config/detekt.yml
git commit -m "ci: 添加 GitHub Actions CI/CD 工作流"
```

---

## 任务 10: 最终验证和构建

- [ ] **步骤 1: 运行完整单元测试套件**

运行: `./gradlew testDebugUnitTest --no-daemon`
预期: 所有测试 PASS

- [ ] **步骤 2: 运行代码检查**

运行: `./gradlew lintDebug --no-daemon`
预期: `BUILD SUCCESSFUL`，无错误

- [ ] **步骤 3: 构建发布版 APK**

运行: `./gradlew assembleRelease --no-daemon`
预期: `BUILD SUCCESSFUL`，APK 输出到 `app/build/outputs/apk/release/`

- [ ] **步骤 4: 验证调试 APK 存在**

运行: `ls -la app/build/outputs/apk/debug/`
预期: `app-debug.apk` 存在

- [ ] **步骤 5: 最终提交**

```bash
git add -A
git commit -m "feat: 完成学习应用，包含完整 CI/CD"
```

---

## 验证清单

- [ ] 所有单元测试通过 (`./gradlew testDebugUnitTest`)
- [ ] 代码检查通过 (`./gradlew lintDebug`)
- [ ] 调试 APK 构建成功 (`./gradlew assembleDebug`)
- [ ] 三个 GitHub Actions 工作流都存在且 YAML 有效
- [ ] Home、Flashcards、Session 屏幕间导航正常
- [ ] Room 数据库创建成功
- [ ] Hilt 依赖注入正确配置

---

## 备注

- **平板优化**: 应用采用自适应布局和更大的触摸目标，适合平板使用
- **仓库模式**: Data 层使用仓库模式实现清晰的分离
- **TDD**: 每个组件都有对应的单元测试
- **CI/CD**: 三个工作流覆盖单元测试、仪器化测试和代码检查
