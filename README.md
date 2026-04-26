# Study App - 安卓平板小学学习应用

[![Android CI](https://github.com/yiranlau/my-study/actions/workflows/android-ci.yml/badge.svg)](https://github.com/yiranlau/my-study/actions/workflows/android-ci.yml)
[![Android Release](https://github.com/yiranlau/my-study/actions/workflows/android-release.yml/badge.svg)](https://github.com/yiranlau/my-study/actions/workflows/android-release.yml)
[![GitHub Release](https://img.shields.io/github/v/release/yiranlau/my-study?include_prereleases&label=release)](https://github.com/yiranlau/my-study/releases)

一款适用于安卓平板的小学学习应用，支持多种题型练习、错题管理和家长管控。

## 功能特性

- 📝 **多种题型** - 支持选择题和填空题练习
- 📚 **专题/年级管理** - 按年级和科目组织学习内容
- 🃏 **错题本** - 自动收集错题，便于针对性复习
- 📊 **学习统计** - 记录练习历史，查看学习进度
- 📁 **CSV 导入** - 批量导入题目，方便教师备课
- 👨‍👩‍👧 **家长模式** - 设备锁屏验证，保护设置
- 📱 **平板优化** - 适配平板大屏，自适应布局

## 技术栈

- **Kotlin** + **Jetpack Compose** - 现代 Android UI
- **MVVM** + **Clean Architecture** - 清晰架构（domain/data/ui 三层）
- **Hilt** - 依赖注入
- **Room** - 本地数据库（8 张表）
- **Jetpack Navigation Compose** - 导航
- **GitHub Actions** - CI/CD 自动化
- **JaCoCo** - 测试覆盖率

## 项目结构

```
app/src/main/java/com/study/app/
├── domain/          # 领域层（模型、仓库接口、用例）
│   ├── model/       # 领域模型
│   └── repository/  # 仓库接口
├── data/           # 数据层（Room 数据库、仓库实现、CSV 导入）
│   ├── local/       # 数据库、DAO、实体
│   └── repository/   # 仓库实现
├── di/             # Hilt 依赖注入模块
└── ui/             # UI 层（Compose 屏幕、ViewModel）
    └── screens/     # 各功能屏幕
```

## 数据库

单数据库 `StudyDatabase` 包含 8 张表：

| 表 | 用途 |
|---|------|
| Subject | 科目 |
| Grade | 年级 |
| Question | 题目 |
| Session | 学习会话 |
| Flashcard | 闪卡 |
| PracticeRecord | 练习记录 |
| ImportRecord | 导入记录 |
| WrongAnswerBook | 错题本 |

## 用户流程

**子模式**：首页 → 选择年级科目 → 设置 → 练习 → 结果

**家长模式**：首页 → 家长入口 →（设备锁屏验证 → 科目/年级管理、CSV 导入、历史记录、导入记录）

## 构建

```bash
./gradlew assembleDebug          # 调试版本
./gradlew assembleRelease       # 发布版本（需设置 VERSION_NAME）
./gradlew testDebugUnitTest     # 单元测试
./gradlew lintDebug              # 代码检查
./gradlew jacocoTestReport       # 覆盖率报告
```

## 测试

```bash
./gradlew testDebugUnitTest        # 单元测试（Mockito + Coroutines test）
./gradlew connectedDebugAndroidTest # 仪器化测试
```

覆盖率报告输出至：`app/build/reports/jacoco/`

## CI/CD

项目使用 GitHub Actions 实现自动化：

- **Android CI** (`android-ci.yml`) - `main` 和 `dev*` 分支：单元测试、Lint、调试构建、JaCoCo 覆盖率、部署到 GitHub Pages
- **Android Release** (`android-release.yml`) - `main` 推送和 `v*` 标签：发布版本构建、创建 GitHub Release
- **GitHub Pages** - 测试报告：https://yiranlaux.github.io/my-study/jacoco/jacocoTestReport/html/

## 辅助脚本

```bash
./android-ci.sh -b  # 通过 CI 构建并下载 APK 到 build/my-study-debug.apk
```
