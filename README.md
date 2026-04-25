# Study App - 安卓平板学习应用

[![Android CI](https://github.com/yiranlau/my-study/actions/workflows/android-ci.yml/badge.svg)](https://github.com/yiranlau/my-study/actions/workflows/android-ci.yml)
[![Android Lint](https://github.com/yiranlau/my-study/actions/workflows/android-lint.yml/badge.svg)](https://github.com/yiranlau/my-study/actions/workflows/android-lint.yml)
[![GitHub Release](https://img.shields.io/github/v/release/yiranlau/my-study?include_prereleases&label=release)](https://github.com/yiranlau/my-study/releases)

一款适用于安卓平板的学习应用，支持学习会话管理和闪卡复习。

## 功能特性

- 📚 **学习会话管理** - 创建和管理学习会话，跟踪学习时长
- 🃏 **闪卡复习** - 创建闪卡，标记已学会的卡片
- 📊 **学习统计** - 查看学习时长和进度
- 📱 **平板优化** - 适配平板大屏，自适应布局

## 技术栈

- **Kotlin** + **Jetpack Compose** - 现代 Android UI
- **MVVM** + **Clean Architecture** - 清晰架构
- **Hilt** - 依赖注入
- **Room** - 本地数据库
- **GitHub Actions** - CI/CD 自动化

## 项目结构

```
app/src/main/java/com/study/app/
├── domain/          # 领域层（模型、用例、仓库接口）
├── data/           # 数据层（Room 数据库、仓库实现）
└── ui/             # UI 层（Compose 屏幕、ViewModel）
```

## 构建

```bash
./gradlew assembleDebug   # 调试版本
./gradlew assembleRelease # 发布版本
```

## 测试

```bash
./gradlew testDebugUnitTest        # 单元测试
./gradlew lintDebug                 # 代码检查
./gradlew connectedDebugAndroidTest # 仪器化测试
```

## CI/CD

项目使用 GitHub Actions 实现自动化：

- **Android CI** - 单元测试、Lint、构建
- **Android Lint** - 代码质量检查
- **Android Instrumented Tests** - 仪器化测试（在模拟器上运行）