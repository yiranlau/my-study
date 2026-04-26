# 小学学习App设计规格

## 1. 概述

**项目名称**：小学学习App
**平台**：安卓平板（横屏专用，分辨率 1280×800）
**核心功能**：支持选择题和填空题的学习应用，家长可录入/导入题目，小孩按学科年级生成练习，做完后展示详细结果。

## 2. 核心流程

```
┌─────────────────────────────────────────────────────────────┐
│                      启动页（模式选择）                         │
│     [👨‍👩‍👧 家长入口]              [👧 小孩入口]              │
│      橙色区域                    绿色区域                     │
└─────────────────────────────────────────────────────────────┘
           │                           │
           ▼                           ▼
      输入密码                      选择学科/年级
           │                           │
           ▼                           ▼
      家长模式                      学习设置
      ┌──────────────────────────────┐  (题量/时间)
      │  侧边导航                     │        │
      │  📚科目 │ 🎓年级 │ ❓题目 │        │
      │  📥导入 │ 📋记录              │        ▼
      └──────────────────────────────┘    开始答题
                                          │
                                          ▼
                                       答题页
                                   (选择题/填空题)
                                          │
                                          ▼
                                       结果页
                                    (详细版)
                                          │
                                          ▼
                                    历史记录 / 错题本
```

### 家长模式导航
- 侧边栏：科目管理 | 年级管理 | 题目管理 | 导入 | 历史记录

### 小孩模式导航
- 学科年级选择 → 学习设置 → 答题 → 结果 → 历史记录/错题本

## 3. 用户角色

### 3.1 家长模式
- **入口**：启动页点击"家长入口" → 输入密码
- **功能**：
  - 科目管理（增删改查，支持语文/数学/英语/自定义科目）
  - 年级管理（增删改查，完全自定义年级名称）
  - 题目录入（选择题：题目+选项+答案；填空题：题目+答案）
  - CSV导入（支持CSV格式批量导入题目）
  - 导入记录（查看历史导入记录，成功/失败统计）
  - 查看历史练习记录

### 3.2 小孩模式
- **入口**：启动页点击"小孩入口"
- **功能**：
  - 选择学科和年级
  - 设置练习参数（题目数量/时间限制）
  - 开始答题（选择题/填空题混合）
  - 查看结果（详细版）
  - 查看历史练习记录
  - 错题本（收藏夹）

## 4. 数据模型

### 4.1 科目 (Subject)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| name | String | 科目名称 |
| isDefault | Boolean | 是否默认科目（语文/数学/英语） |
| createdAt | Long | 创建时间 |

### 4.2 年级 (Grade)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| name | String | 年级名称 |
| order | Int | 排序 |
| createdAt | Long | 创建时间 |

### 4.3 题目 (Question)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| subjectId | Long | 科目ID |
| gradeId | Long | 年级ID |
| type | Enum | CHOICE(选择题)/FILL_BLANK(填空题) |
| content | String | 题目内容 |
| options | String? | 选项(JSON数组，仅选择题) |
| answer | String | 答案 |
| hint | String? | 答案提示（仅填空题，可选） |
| createdAt | Long | 创建时间 |

### 4.4 错题本 (WrongAnswerBook)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| questionId | Long | 题目ID |
| studentAnswer | String | 学生的错误答案 |
| correctAnswer | String | 正确答案 |
| addedAt | Long | 收藏时间 |

**说明**：错题本自动收录答错的题目，可手动移除。

### 4.5 练习记录 (PracticeRecord)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| subjectId | Long | 科目ID |
| gradeId | Long | 年级ID |
| totalQuestions | Int | 总题数 |
| correctCount | Int | 正确数 |
| durationMillis | Long | 耗时(毫秒) |
| questionResults | String | 每题结果(JSON数组) |
| createdAt | Long | 创建时间 |

### 4.6 导入记录 (ImportRecord)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| fileName | String | 原始文件名 |
| totalCount | Int | 总题目数 |
| successCount | Int | 成功导入数 |
| failCount | Int | 失败数 |
| createdAt | Long | 导入时间 |

### 4.7 CSV题目格式
```csv
type,subject,grade,content,options,answer
CHOICE,数学,一年级,1+1=?,["2","3","4","5"],A
FILL_BLANK,语文,二年级,中国的首都是?,,北京
```

**说明**：
- `type`：CHOICE 或 FILL_BLANK
- `subject`：科目名称（不存在则自动创建）
- `grade`：年级名称（不存在则自动创建）
- `content`：题目内容
- `options`：选项（仅选择题，JSON数组格式，可选）
- `answer`：答案（选择题用A/B/C/D，填空题用实际答案）

## 5. 界面设计

### 5.1 启动页
- **布局**：左右分栏，橙色(家长)/绿色(小孩)并排
- **家长入口**：大按钮，点击需输入密码
- **小孩入口**：大按钮，点击直接进入
- **配色**：温暖 playground 风格，圆角大按钮

### 5.2 家长模式 - 侧边导航 + 科目管理
- **布局**：左侧280px侧边栏 + 右侧内容区
- **侧边栏**：Logo + 5个导航项（科目/年级/题目/导入/记录）
- **内容区**：3列科目网格卡片
- **卡片内容**：科目图标、名称、题目数量
- **操作**：点击卡片进入详情，增删改查

### 5.3 家长模式 - 年级管理
- 同5.2布局，年级列表展示

### 5.4 家长模式 - 题目录入
- **布局**：左侧表单区 + 右侧已录入列表
- **类型切换**：选择题/填空题 Tab切换
- **选择题表单**：
  - 题目内容（多行文本）
  - A/B/C/D选项编辑（点击字母设为正确答案）
  - 正确答案下拉选择
- **填空题表单**：
  - 题目内容（使用`_____`表示空白）
  - 填空答案（必填）
  - 答案提示（可选，紫色主题区分）
- **右侧列表**：已录入题目预览，选中可编辑/删除

### 5.5 家长模式 - CSV导入
- **布局**：左侧上传区 + 右侧预览区
- **上传区**：
  - 拖拽/点击上传CSV文件
  - CSV格式说明表格
- **预览区**：
  - 导入题目列表（类型/内容/科目/答案）
  - 每题显示成功✓/失败✗状态
  - 确认导入按钮
  - 下载示例CSV按钮

### 5.6 家长模式 - 导入记录
- **布局**：顶部统计 + 筛选 + 列表
- **统计概览**：总导入次数、总题目数
- **日期筛选**：全部/今天/本周/本月
- **记录列表**：
  - 文件名、日期、涉及科目
  - 成功/失败题目数统计
  - 重新导入/删除操作

### 5.7 家长模式 - 历史记录
- **布局**：顶部标题 + 列表
- **列表项**：图标、学科年级、题数、耗时、正确率
- **正确率颜色**：绿色(≥80%)/黄色(60-79%)/红色(<60%)

### 5.8 小孩模式 - 学科年级选择
- **布局**：左侧品牌区 + 右侧选择区
- **左侧**：大图标 + 标题"选择学科和年级"
- **右侧**：
  - 学科选择Chip标签
  - 年级选择Chip标签
  - 下一步按钮

### 5.9 学习设置
- **布局**：居中卡片
- **设置项**：
  - 题目数量：滑块(1-50)，显示当前值
  - 时间限制：开关 + 分钟输入
- **开始按钮**：底部，绿色渐变

### 5.10 答题页 - 选择题
- **布局**：左侧题目区 + 右侧进度区
- **左侧**：
  - 顶部：进度条 + 计时器
  - 题目卡片：类型Badge + 题目内容
  - 选项网格：2×2布局，A/B/C/D按钮
- **右侧**：
  - 答题进度指示器（1-10编号）
  - 已答题目高亮
  - 当前题目红色边框
  - 已用时间统计

### 5.11 答题页 - 填空题
- **布局**：同5.10
- **主题色**：紫色（区分选择题的橙色）
- **左侧**：
  - 填空题Badge
  - 题目内容（`_____`显示为下划线）
  - 大输入框 + 提交按钮

### 5.12 结果页（详细版）
- **布局**：左侧统计 + 右侧详情
- **左侧统计卡片**：
  - 总题数
  - 答对题数
  - 总耗时
  - 正确率（环形进度图）
- **右侧答题详情**：
  - 每题：✓/✗状态、内容、你的答案、正确答案、该题用时
  - 每题可加入/移除错题本
- **底部按钮**：返回 / 重新学习

### 5.13 错题本页面
- **布局**：顶部筛选 + 列表 + 底部操作
- **筛选**：全部/语文/数学/英语 Chip
- **列表项**：
  - 编号、题目内容
  - 你的错答（红色）/ 正确答案（绿色）
  - 学科/年级标签
  - 移除按钮
- **底部**：重新练习错题按钮

### 5.14 历史记录页面
- **布局**：顶部标题 + 统计 + 列表
- **统计**：总练习次数、总题目数
- **列表项**：
  - 学科年级
  - 题数、耗时、日期
  - 正确率Badge

## 6. 技术架构

沿用现有Clean Architecture结构：

```
app/src/main/java/com/study/app/
├── domain/
│   ├── model/          # 领域模型
│   ├── repository/     # 仓库接口
│   └── usecase/       # 用例
├── data/
│   ├── local/         # Room数据库
│   └── repository/    # 仓库实现
└── ui/
    ├── screens/       # 页面
    ├── components/     # 组件
    └── viewmodel/      # ViewModel
```

### 新增模块
- `domain/model/Question.kt` - 题目模型
- `domain/model/PracticeRecord.kt` - 练习记录模型
- `domain/model/ImportRecord.kt` - 导入记录模型
- `domain/repository/QuestionRepository.kt` - 题目仓库
- `domain/repository/PracticeRepository.kt` - 练习记录仓库
- `domain/repository/ImportRepository.kt` - 导入记录仓库
- `data/local/QuestionDao.kt` - 题目DAO
- `data/local/PracticeDao.kt` - 练习记录DAO
- `data/local/ImportDao.kt` - 导入记录DAO
- `data/local/entity/QuestionEntity.kt` - 题目实体
- `data/local/entity/PracticeEntity.kt` - 练习记录实体
- `data/local/entity/ImportEntity.kt` - 导入记录实体
- `data/repository/QuestionRepositoryImpl.kt` - 题目仓库实现
- `data/repository/PracticeRepositoryImpl.kt` - 练习记录仓库实现
- `data/repository/ImportRepositoryImpl.kt` - 导入记录仓库实现
- `data/import/CsvImporter.kt` - CSV导入器
- `ui/screens/parent/` - 家长模式页面
  - `ParentHomeScreen.kt` - 家长模式主页（侧边导航）
  - `SubjectManagementScreen.kt` - 科目管理
  - `GradeManagementScreen.kt` - 年级管理
  - `QuestionEntryScreen.kt` - 题目录入
  - `CsvImportScreen.kt` - CSV导入
  - `ImportRecordsScreen.kt` - 导入记录
  - `HistoryRecordsScreen.kt` - 历史记录
- `ui/screens/child/` - 小孩模式页面
  - `SubjectGradeSelectScreen.kt` - 学科年级选择
  - `SettingsScreen.kt` - 学习设置
- `ui/screens/quiz/` - 答题页面
  - `QuizScreen.kt` - 答题（含选择题/填空题）
- `ui/screens/result/` - 结果页面
  - `ResultScreen.kt` - 结果页（详细版）
  - `WrongBookScreen.kt` - 错题本
  - `ChildHistoryScreen.kt` - 小孩历史记录

## 7. 依赖变更

### 新增依赖
- Apache Commons CSV（CSV解析）
- Kotlinx Serialization（JSON序列化）

### build.gradle.kts 修改
```kotlin
dependencies {
    // 新增
    implementation("org.apache.commons:commons-csv:1.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}
```

## 8. 实现计划

### Phase 1: 数据层
1. 创建Question、PracticeRecord、ImportRecord实体
2. 创建DAO和Database
3. 实现仓库接口和实现
4. 实现CSV导入器

### Phase 2: 家长模式
1. 创建家长模式导航（侧边栏）
2. 实现科目管理（CRUD）
3. 实现年级管理（CRUD）
4. 实现题目管理（CRUD）
5. 实现CSV导入页面
6. 实现导入记录页面

### Phase 3: 小孩模式
1. 实现学科年级选择页面
2. 实现学习设置页面
3. 实现答题页面（选择题/填空题）
4. 实现结果页面（详细版）
5. 实现错题本页面
6. 实现历史记录页面

## 9. UI设计规范

### 9.1 配色方案
| 用途 | 颜色 | 色值 |
|------|------|------|
| 主色（家长/选择题） | 温暖橙 | #FF9B50 |
| 辅助色（小孩/成功） | 薄荷绿 | #7ED7C1 |
| 强调色（错误） | 珊瑚红 | #FF6B6B |
| 填空题主题 | 淡紫 | #A29BFE |
| 时间/警告 | 暖黄 | #FFEAA7 |
| 历史记录 | 平静蓝 | #74B9FF |

### 9.2 圆角规范
| 元素 | 圆角 |
|------|------|
| 小按钮/Chip | 12px |
| 中卡片/导航项 | 20px |
| 大卡片/题目卡片 | 28px |
| 特大按钮/模式入口 | 40px |

### 9.3 触控规范
- 最小触控区域：44×44px
- 推荐触控区域：48×48px以上
- 按钮高度：≥48px
- 选项按钮高度：≥56px

## 10. 密码存储

- 初始密码：`123456`
- 密码使用SHA-256哈希后存储
- 首次使用后可修改密码
- 密码存储在SharedPreferences（加密）

## 11. 界面文件索引

UI设计原型：`docs/superpowers/specs/2026-04-25-learning-app-ui-design.html`

| 页面编号 | 页面名称 | 说明 |
|----------|----------|------|
| 1 | 启动页 | 家长/小孩入口 |
| 2 | 家长模式主页 | 侧边导航+科目管理 |
| 3 | 学科年级选择 | 小孩模式入口 |
| 4 | 学习设置 | 题量/时间配置 |
| 5 | 选择题答题 | 标准答题界面 |
| 5b | 填空题答题 | 紫色主题 |
| 6 | 结果页 | 详细统计+答题详情 |
| 6b | 题目录入 | 选择题/填空题表单 |
| 6c | CSV导入 | 拖拽上传+预览 |
| 6d | 导入记录 | 历史导入统计 |
| 7 | 错题本 | 错题列表+重练 |
| 8 | 历史记录 | 小孩练习历史 |
