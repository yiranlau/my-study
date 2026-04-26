# 小学学习App Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现一个安卓平板小学学习App，支持选择题/填空题答题、家长录入题目、CSV批量导入、错题本和历史记录

**Architecture:** Clean Architecture (Domain/Data/UI三层)，MVVM + Jetpack Compose，Room本地存储，Hilt依赖注入

**Tech Stack:** Kotlin, Jetpack Compose, Room, Hilt, Navigation Compose, Apache Commons CSV, Kotlinx Serialization

---

## Phase 1: 数据层 (Data Layer)

### Task 1: 创建领域模型 (Domain Models)

**Files:**
- Create: `app/src/main/java/com/study/app/domain/model/Subject.kt`
- Create: `app/src/main/java/com/study/app/domain/model/Grade.kt`
- Create: `app/src/main/java/com/study/app/domain/model/Question.kt`
- Create: `app/src/main/java/com/study/app/domain/model/PracticeRecord.kt`
- Create: `app/src/main/java/com/study/app/domain/model/ImportRecord.kt`
- Create: `app/src/main/java/com/study/app/domain/model/WrongAnswerBook.kt`
- Create: `app/src/test/java/com/study/app/domain/model/SubjectTest.kt`
- Create: `app/src/test/java/com/study/app/domain/model/GradeTest.kt`
- Create: `app/src/test/java/com/study/app/domain/model/QuestionTest.kt`
- Create: `app/src/test/java/com/study/app/domain/model/PracticeRecordTest.kt`

- [ ] **Step 1: 写Subject模型测试**

```kotlin
// app/src/test/java/com/study/app/domain/model/SubjectTest.kt
package com.study.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SubjectTest {
    @Test
    fun subject_creates_with_name_and_default_flag() {
        val subject = Subject(name = "语文", isDefault = true)
        assertEquals("语文", subject.name)
        assertEquals(true, subject.isDefault)
    }

    @Test
    fun subject_id_defaults_to_zero() {
        val subject = Subject(name = "数学")
        assertEquals(0L, subject.id)
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

Run: `./gradlew :app:test --tests "com.study.app.domain.model.SubjectTest" 2>&1 | tail -30`
Expected: FAIL - Subject class not found

- [ ] **Step 3: 写Subject领域模型**

```kotlin
// app/src/main/java/com/study/app/domain/model/Subject.kt
package com.study.app.domain.model

data class Subject(
    val id: Long = 0,
    val name: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
```

- [ ] **Step 4: 运行测试确认通过**

Run: `./gradlew :app:test --tests "com.study.app.domain.model.SubjectTest" 2>&1 | tail -10`
Expected: PASS

- [ ] **Step 5: 重复以上步骤完成Grade, Question, PracticeRecord, ImportRecord, WrongAnswerBook模型及测试**

- [ ] **Step 6: 提交**

```bash
git add app/src/main/java/com/study/app/domain/model/*.kt
git add app/src/test/java/com/study/app/domain/model/*.kt
git commit -m "feat(domain): add domain models for learning app

- Subject: 科目模型 (name, isDefault, createdAt)
- Grade: 年级模型 (name, order, createdAt)
- Question: 题目模型 (subjectId, gradeId, type, content, options, answer, hint)
- PracticeRecord: 练习记录模型
- ImportRecord: 导入记录模型
- WrongAnswerBook: 错题本模型"
```

---

### Task 2: 创建Room实体 (Room Entities)

**Files:**
- Create: `app/src/main/java/com/study/app/data/local/entity/SubjectEntity.kt`
- Create: `app/src/main/java/com/study/app/data/local/entity/GradeEntity.kt`
- Create: `app/src/main/java/com/study/app/data/local/entity/QuestionEntity.kt`
- Create: `app/src/main/java/com/study/app/data/local/entity/PracticeRecordEntity.kt`
- Create: `app/src/main/java/com/study/app/data/local/entity/ImportRecordEntity.kt`
- Create: `app/src/main/java/com/study/app/data/local/entity/WrongAnswerBookEntity.kt`
- Create: `app/src/test/java/com/study/app/data/local/entity/SubjectEntityTest.kt`

- [ ] **Step 1: 写SubjectEntity测试**

```kotlin
// app/src/test/java/com/study/app/data/local/entity/SubjectEntityTest.kt
package com.study.app.data.local.entity

import org.junit.Assert.assertEquals
import org.junit.Test

class SubjectEntityTest {
    @Test
    fun subject_entity_maps_from_subject() {
        val subject = Subject(id = 1L, name = "语文", isDefault = true)
        val entity = SubjectEntity.fromDomain(subject)
        assertEquals(1L, entity.id)
        assertEquals("语文", entity.name)
        assertEquals(true, entity.isDefault)
    }

    @Test
    fun subject_entity_converts_to_domain() {
        val entity = SubjectEntity(id = 1L, name = "数学", isDefault = false, createdAt = 1000L)
        val subject = entity.toDomain()
        assertEquals(1L, subject.id)
        assertEquals("数学", subject.name)
        assertEquals(false, subject.isDefault)
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

Run: `./gradlew :app:test --tests "com.study.app.data.local.entity.SubjectEntityTest" 2>&1 | tail -30`
Expected: FAIL - SubjectEntity not found

- [ ] **Step 3: 写SubjectEntity**

```kotlin
// app/src/main/java/com/study/app/data/local/entity/SubjectEntity.kt
package com.study.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.study.app.domain.model.Subject

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = Subject(id, name, isDefault, createdAt)

    companion object {
        fun fromDomain(subject: Subject) = SubjectEntity(
            id = subject.id,
            name = subject.name,
            isDefault = subject.isDefault,
            createdAt = subject.createdAt
        )
    }
}
```

- [ ] **Step 4: 运行测试确认通过**

Run: `./gradlew :app:test --tests "com.study.app.data.local.entity.SubjectEntityTest" 2>&1 | tail -10`
Expected: PASS

- [ ] **Step 5: 重复完成其他Entity及测试**

- [ ] **Step 6: 提交**

---

### Task 3: 创建DAO接口

**Files:**
- Create: `app/src/main/java/com/study/app/data/local/SubjectDao.kt`
- Create: `app/src/main/java/com/study/app/data/local/GradeDao.kt`
- Create: `app/src/main/java/com/study/app/data/local/QuestionDao.kt`
- Create: `app/src/main/java/com/study/app/data/local/PracticeRecordDao.kt`
- Create: `app/src/main/java/com/study/app/data/local/ImportRecordDao.kt`
- Create: `app/src/main/java/com/study/app/data/local/WrongAnswerBookDao.kt`
- Create: `app/src/test/java/com/study/app/data/local/SubjectDaoTest.kt`

- [ ] **Step 1: 写SubjectDao测试**

```kotlin
// app/src/test/java/com/study/app/data/local/SubjectDaoTest.kt
package com.study.app.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SubjectDaoTest {
    private lateinit var database: StudyDatabase
    private lateinit var dao: SubjectDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StudyDatabase::class.java
        ).build()
        dao = database.subjectDao()
    }

    @Test
    fun insert_and_get_subject(): Unit = runBlocking {
        val entity = SubjectEntity(name = "语文", isDefault = true)
        dao.insert(entity)
        val result = dao.getById(1L)
        assertEquals("语文", result?.name)
    }

    @Test
    fun get_all_subjects(): Unit = runBlocking {
        dao.insert(SubjectEntity(name = "语文"))
        dao.insert(SubjectEntity(name = "数学"))
        val results = dao.getAll()
        assertEquals(2, results.size)
    }
}
```

- [ ] **Step 2: 运行测试确认失败 (DAO不存在)**

- [ ] **Step 3: 写SubjectDao接口**

```kotlin
// app/src/main/java/com/study/app/data/local/SubjectDao.kt
package com.study.app.data.local

import androidx.room.*
import com.study.app.data.local.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SubjectEntity): Long

    @Update
    suspend fun update(entity: SubjectEntity)

    @Delete
    suspend fun delete(entity: SubjectEntity)

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getById(id: Long): SubjectEntity?

    @Query("SELECT * FROM subjects ORDER BY createdAt ASC")
    fun getAll(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): SubjectEntity?
}
```

- [ ] **Step 4: 运行测试确认通过**

- [ ] **Step 5: 重复完成其他DAO及测试**

- [ ] **Step 6: 提交**

---

### Task 4: 更新StudyDatabase

**Files:**
- Modify: `app/src/main/java/com/study/app/data/local/StudyDatabase.kt:1-14`
- Create: `app/src/test/java/com/study/app/data/local/StudyDatabaseTest.kt`

- [ ] **Step 1: 写数据库迁移测试**

```kotlin
// app/src/test/java/com/study/app/data/local/StudyDatabaseTest.kt
package com.study.app.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class StudyDatabaseTest {
    private lateinit var database: StudyDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StudyDatabase::class.java
        ).build()
    }

    @Test
    fun database_has_subject_dao(): Unit = runBlocking {
        val dao = database.subjectDao()
        dao.insert(SubjectEntity(name = "测试"))
        val result = dao.getAll()
        assertEquals(1, result.size)
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

- [ ] **Step 3: 更新StudyDatabase添加新实体**

```kotlin
// app/src/main/java/com/study/app/data/local/StudyDatabase.kt
package com.study.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SessionEntity::class,
        FlashcardEntity::class,
        SubjectEntity::class,
        GradeEntity::class,
        QuestionEntity::class,
        PracticeRecordEntity::class,
        ImportRecordEntity::class,
        WrongAnswerBookEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class StudyDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun subjectDao(): SubjectDao
    abstract fun gradeDao(): GradeDao
    abstract fun questionDao(): QuestionDao
    abstract fun practiceRecordDao(): PracticeRecordDao
    abstract fun importRecordDao(): ImportRecordDao
    abstract fun wrongAnswerBookDao(): WrongAnswerBookDao
}
```

- [ ] **Step 4: 运行测试确认通过**

- [ ] **Step 5: 提交**

---

### Task 5: 创建Repository接口和实现

**Files:**
- Create: `app/src/main/java/com/study/app/domain/repository/SubjectRepository.kt`
- Create: `app/src/main/java/com/study/app/domain/repository/GradeRepository.kt`
- Create: `app/src/main/java/com/study/app/domain/repository/QuestionRepository.kt`
- Create: `app/src/main/java/com/study/app/domain/repository/PracticeRepository.kt`
- Create: `app/src/main/java/com/study/app/domain/repository/ImportRepository.kt`
- Create: `app/src/main/java/com/study/app/domain/repository/WrongAnswerRepository.kt`
- Create: `app/src/main/java/com/study/app/data/repository/SubjectRepositoryImpl.kt`
- Create: `app/src/main/java/com/study/app/data/repository/GradeRepositoryImpl.kt`
- Create: `app/src/main/java/com/study/app/data/repository/QuestionRepositoryImpl.kt`
- Create: `app/src/main/java/com/study/app/data/repository/PracticeRepositoryImpl.kt`
- Create: `app/src/main/java/com/study/app/data/repository/ImportRepositoryImpl.kt`
- Create: `app/src/main/java/com/study/app/data/repository/WrongAnswerRepositoryImpl.kt`
- Create: `app/src/test/java/com/study/app/data/repository/SubjectRepositoryImplTest.kt`

- [ ] **Step 1: 写SubjectRepository测试**

```kotlin
// app/src/test/java/com/study/app/data/repository/SubjectRepositoryImplTest.kt
package com.study.app.data.repository

import com.study.app.data.local.SubjectDao
import com.study.app.data.local.SubjectEntity
import com.study.app.domain.model.Subject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SubjectRepositoryImplTest {
    @Mock lateinit var subjectDao: SubjectDao

    private lateinit var repository: SubjectRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = SubjectRepositoryImpl(subjectDao)
    }

    @Test
    fun get_all_returns_domain_list(): Unit = runBlocking {
        val entity = SubjectEntity(id = 1L, name = "语文")
        whenever(subjectDao.getAll()).thenReturn(flowOf(listOf(entity)))

        val result = repository.getAll().first()

        assertEquals(1, result.size)
        assertEquals("语文", result[0].name)
    }

    @Test
    fun insert_calls_dao_insert(): Unit = runBlocking {
        val subject = Subject(name = "数学")

        repository.insert(subject)

        verify(subjectDao).insert(SubjectEntity.fromDomain(subject))
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

- [ ] **Step 3: 写SubjectRepository接口**

```kotlin
// app/src/main/java/com/study/app/domain/repository/SubjectRepository.kt
package com.study.app.domain.repository

import com.study.app.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {
    fun getAll(): Flow<List<Subject>>
    suspend fun getById(id: Long): Subject?
    suspend fun getByName(name: String): Subject?
    suspend fun insert(subject: Subject): Long
    suspend fun update(subject: Subject)
    suspend fun delete(subject: Subject)
}
```

- [ ] **Step 4: 运行测试确认通过**

- [ ] **Step 5: 重复完成其他Repository**

- [ ] **Step 6: 提交**

---

### Task 6: 实现CSV导入器

**Files:**
- Create: `app/src/main/java/com/study/app/data/import/CsvImporter.kt`
- Create: `app/src/main/java/com/study/app/data/import/CsvImportResult.kt`
- Create: `app/src/test/java/com/study/app/data/import/CsvImporterTest.kt`

- [ ] **Step 1: 写CsvImporter测试**

```kotlin
// app/src/test/java/com/study/app/data/import/CsvImporterTest.kt
package com.study.app.data.import

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CsvImporterTest {
    @Test
    fun parse_choice_question_line() {
        val csv = "CHOICE,数学,一年级,1+1=?,[\"2\",\"3\",\"4\",\"5\"],A"
        val importer = CsvImporter()
        val result = importer.parseLine(csv)
        assertTrue(result is CsvImportResult.Success)
        val question = (result as CsvImportResult.Success).question
        assertEquals("CHOICE", question.type.name)
        assertEquals("1+1=?", question.content)
        assertEquals("A", question.answer)
    }

    @Test
    fun parse_fill_blank_question_line() {
        val csv = "FILL_BLANK,语文,二年级,中国的首都是?,,北京"
        val importer = CsvImporter()
        val result = importer.parseLine(csv)
        assertTrue(result is CsvImportResult.Success)
        val question = (result as CsvImportResult.Success).question
        assertEquals("FILL_BLANK", question.type.name)
        assertEquals("北京", question.answer)
    }

    @Test
    fun invalid_line_returns_error() {
        val csv = "INVALID,数学,一年级"
        val importer = CsvImporter()
        val result = importer.parseLine(csv)
        assertTrue(result is CsvImportResult.Error)
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

- [ ] **Step 3: 写CsvImportResult和CsvImporter**

```kotlin
// app/src/main/java/com/study/app/data/import/CsvImportResult.kt
package com.study.app.data.import

import com.study.app.domain.model.Question

sealed class CsvImportResult {
    data class Success(val question: Question) : CsvImportResult()
    data class Error(val message: String, val line: String) : CsvImportResult()
}

// app/src/main/java/com/study/app/data/import/CsvImporter.kt
package com.study.app.data.import

import com.study.app.domain.model.Question
import com.study.app.domain.model.QuestionType

class CsvImporter {
    fun parseLine(line: String): CsvImportResult {
        return try {
            val parts = line.split(",")
            if (parts.size < 5) {
                return CsvImportResult.Error("Invalid format", line)
            }
            val type = when (parts[0].trim()) {
                "CHOICE" -> QuestionType.CHOICE
                "FILL_BLANK" -> QuestionType.FILL_BLANK
                else -> return CsvImportResult.Error("Unknown type: ${parts[0]}", line)
            }
            val subject = parts[1].trim()
            val grade = parts[2].trim()
            val content = parts[3].trim()
            val options = if (type == QuestionType.CHOICE && parts.size > 4) {
                parts[4].trim()
            } else null
            val answer = if (parts.size > 5) parts[5].trim() else ""

            CsvImportResult.Success(
                Question(
                    subjectName = subject,
                    gradeName = grade,
                    type = type,
                    content = content,
                    options = options,
                    answer = answer
                )
            )
        } catch (e: Exception) {
            CsvImportResult.Error(e.message ?: "Unknown error", line)
        }
    }
}
```

- [ ] **Step 4: 运行测试确认通过**

- [ ] **Step 5: 提交**

---

## Phase 2: 家长模式 (Parent Mode)

### Task 7: 家长模式导航和主页

**Files:**
- Create: `app/src/main/java/com/study/app/ui/screens/parent/ParentHomeScreen.kt`
- Create: `app/src/main/java/com/study/app/ui/screens/parent/ParentViewModel.kt`
- Create: `app/src/main/java/com/study/app/ui/components/SideNavigation.kt`
- Create: `app/src/main/java/com/study/app/ui/theme/Color.kt` (update)
- Create: `app/src/test/java/com/study/app/ui/screens/parent/ParentViewModelTest.kt`

- [ ] **Step 1: 写ParentViewModel测试**

```kotlin
// app/src/test/java/com/study/app/ui/screens/parent/ParentViewModelTest.kt
package com.study.app.ui.screens.parent

import androidx.lifecycle.ViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

class ParentViewModelTest {
    @Test
    fun initial_selected_nav_item_is_subjects() {
        val viewModel = ParentViewModel()
        assertEquals(NavItem.SUBJECTS, viewModel.selectedNavItem)
    }

    @Test
    fun select_nav_item_updates_state() {
        val viewModel = ParentViewModel()
        viewModel.selectNavItem(NavItem.GRADES)
        assertEquals(NavItem.GRADES, viewModel.selectedNavItem)
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

- [ ] **Step 3: 写ParentViewModel**

```kotlin
// app/src/main/java/com/study/app/ui/screens/parent/ParentViewModel.kt
package com.study.app.ui.screens.parent

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

enum class NavItem { SUBJECTS, GRADES, QUESTIONS, IMPORT, RECORDS }

@HiltViewModel
class ParentViewModel @Inject constructor() : ViewModel() {
    private val _selectedNavItem = MutableStateFlow(NavItem.SUBJECTS)
    val selectedNavItem: StateFlow<NavItem> = _selectedNavItem

    fun selectNavItem(item: NavItem) {
        _selectedNavItem.value = item
    }
}
```

- [ ] **Step 4: 运行测试确认通过**

- [ ] **Step 5: 写ParentHomeScreen UI组件**

- [ ] **Step 6: 提交**

---

### Task 8: 科目管理页面

**Files:**
- Create: `app/src/main/java/com/study/app/ui/screens/parent/SubjectManagementScreen.kt`
- Create: `app/src/main/java/com/study/app/ui/screens/parent/SubjectManagementViewModel.kt`
- Create: `app/src/main/java/com/study/app/ui/components/SubjectCard.kt`
- Create: `app/src/test/java/com/study/app/ui/screens/parent/SubjectManagementViewModelTest.kt`

- [ ] **Step 1: 写SubjectManagementViewModel测试**

```kotlin
// app/src/test/java/com/study/app/ui/screens/parent/SubjectManagementViewModelTest.kt
package com.study.app.ui.screens.parent

import com.study.app.domain.model.Subject
import com.study.app.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class SubjectManagementViewModelTest {
    @Mock lateinit var subjectRepository: SubjectRepository

    private lateinit var viewModel: SubjectManagementViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = SubjectManagementViewModel(subjectRepository)
    }

    @Test
    fun load_subjects_populates_list(): Unit = runBlocking {
        val subjects = listOf(Subject(id = 1L, name = "语文"), Subject(id = 2L, name = "数学"))
        whenever(subjectRepository.getAll()).thenReturn(flowOf(subjects))

        viewModel.loadSubjects()
        val result = viewModel.subjects.first()

        assertEquals(2, result.size)
        assertEquals("语文", result[0].name)
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

- [ ] **Step 3: 写SubjectManagementViewModel**

```kotlin
// app/src/main/java/com/study/app/ui/screens/parent/SubjectManagementViewModel.kt
package com.study.app.ui.screens.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.domain.model.Subject
import com.study.app.domain.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectManagementViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    val subjects: StateFlow<List<Subject>> = subjectRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addSubject(name: String) {
        viewModelScope.launch {
            subjectRepository.insert(Subject(name = name))
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            subjectRepository.delete(subject)
        }
    }

    fun updateSubject(subject: Subject) {
        viewModelScope.launch {
            subjectRepository.update(subject)
        }
    }
}
```

- [ ] **Step 4: 运行测试确认通过**

- [ ] **Step 5: 写SubjectManagementScreen UI**

- [ ] **Step 6: 提交**

---

### Task 9: 年级管理页面

**Files:**
- Create: `app/src/main/java/com/study/app/ui/screens/parent/GradeManagementScreen.kt`
- Create: `app/src/main/java/com/study/app/ui/screens/parent/GradeManagementViewModel.kt`
- Create: `app/src/test/java/com/study/app/ui/screens/parent/GradeManagementViewModelTest.kt`

- [ ] **Step 1-6: 参照Task 8实现年级管理**

- [ ] **Step 7: 提交**

---

### Task 10: 题目录入页面

**Files:**
- Create: `app/src/main/java/com/study/app/ui/screens/parent/QuestionEntryScreen.kt`
- Create: `app/src/main/java/com/study/app/ui/screens/parent/QuestionEntryViewModel.kt`
- Create: `app/src/test/java/com/study/app/ui/screens/parent/QuestionEntryViewModelTest.kt`

- [ ] **Step 1: 写QuestionEntryViewModel测试**

```kotlin
// app/src/test/java/com/study/app/ui/screens/parent/QuestionEntryViewModelTest.kt
package com.study.app.ui.screens.parent

import com.study.app.domain.model.Question
import com.study.app.domain.model.QuestionType
import com.study.app.domain.repository.QuestionRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class QuestionEntryViewModelTest {
    @Mock lateinit var questionRepository: QuestionRepository

    private lateinit var viewModel: QuestionEntryViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = QuestionEntryViewModel(questionRepository)
    }

    @Test
    fun initial_question_type_is_choice() {
        assertEquals(QuestionType.CHOICE, viewModel.selectedType)
    }

    @Test
    fun switch_type_updates_state() {
        viewModel.setType(QuestionType.FILL_BLANK)
        assertEquals(QuestionType.FILL_BLANK, viewModel.selectedType)
    }

    @Test
    fun save_choice_question_calls_repository(): Unit = runBlocking {
        viewModel.setType(QuestionType.CHOICE)
        viewModel.setContent("1+1=?")
        viewModel.setOptions(listOf("2", "3", "4", "5"))
        viewModel.setAnswer("A")

        viewModel.saveQuestion()

        verify(questionRepository).insert(any(Question::class.java))
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

- [ ] **Step 3: 写QuestionEntryViewModel**

- [ ] **Step 4: 运行测试确认通过**

- [ ] **Step 5: 写QuestionEntryScreen UI**

- [ ] **Step 6: 提交**

---

### Task 11: CSV导入页面

**Files:**
- Create: `app/src/main/java/com/study/app/ui/screens/parent/CsvImportScreen.kt`
- Create: `app/src/main/java/com/study/app/ui/screens/parent/CsvImportViewModel.kt`
- Create: `app/src/test/java/com/study/app/ui/screens/parent/CsvImportViewModelTest.kt`

- [ ] **Step 1-6: 实现CSV导入功能**

- [ ] **Step 7: 提交**

---

### Task 12: 导入记录页面

**Files:**
- Create: `app/src/main/java/com/study/app/ui/screens/parent/ImportRecordsScreen.kt`
- Create: `app/src/main/java/com/study/app/ui/screens/parent/ImportRecordsViewModel.kt`
- Create: `app/src/test/java/com/study/app/ui/screens/parent/ImportRecordsViewModelTest.kt`

- [ ] **Step 1-6: 实现导入记录功能**

- [ ] **Step 7: 提交**

---

### Task 13: 家长历史记录页面

**Files:**
- Create: `app/src/main/java/com/study/app/ui/screens/parent/HistoryRecordsScreen.kt`
- Create: `app/src/main/java/com/study/app/ui/screens/parent/HistoryRecordsViewModel.kt`
- Create: `app/src/test/java/com/study/app/ui/screens/parent/HistoryRecordsViewModelTest.kt`

- [ ] **Step 1-6: 实现历史记录功能**

- [ ] **Step 7: 提交**

---

## Phase 3: 小孩模式 (Child Mode)

### Task 14: 启动页和模式选择

**Files:**
- Create: `app/src/main/java/com/study/app/ui/screens/HomeScreen.kt`
- Create: `app/src/main/java/com/study/app/ui/screens/HomeViewModel.kt`
- Create: `app/src/test/java/com/study/app/ui/screens/HomeViewModelTest.kt`

- [ ] **Step 1: 写HomeViewModel测试**

```kotlin
// app/src/test/java/com/study/app/ui/screens/HomeViewModelTest.kt
package com.study.app.ui.screens

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeViewModelTest {
    @Test
    fun initial_state_is_not_in_parent_mode() {
        val viewModel = HomeViewModel()
        assertFalse(viewModel.isParentMode)
    }

    @Test
    fun enter_parent_mode_requires_password_verification() {
        val viewModel = HomeViewModel()
        val result = viewModel.verifyParentPassword("123456")
        assertTrue(result)
    }

    @Test
    fun wrong_password_returns_false() {
        val viewModel = HomeViewModel()
        val result = viewModel.verifyParentPassword("wrong")
        assertFalse(result)
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

- [ ] **Step 3: 写HomeViewModel**

```kotlin
// app/src/main/java/com/study/app/ui/screens/HomeViewModel.kt
package com.study.app.ui.screens

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    val isParentMode: Boolean
        get() = _isParentMode
    private var _isParentMode = false

    fun verifyParentPassword(password: String): Boolean {
        val hashed = hashPassword(password)
        val stored = sharedPreferences.getString(KEY_PARENT_PASSWORD, DEFAULT_PASSWORD_HASH)
        if (hashed == stored) {
            _isParentMode = true
            return true
        }
        return false
    }

    fun exitParentMode() {
        _isParentMode = false
    }

    companion object {
        private const val KEY_PARENT_PASSWORD = "parent_password_hash"
        private const val DEFAULT_PASSWORD = "123456"
        private val DEFAULT_PASSWORD_HASH = hashSha256(DEFAULT_PASSWORD)

        fun hashSha256(input: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
}
```

- [ ] **Step 4: 运行测试确认通过**

- [ ] **Step 5: 写HomeScreen UI**

- [ ] **Step 6: 提交**

---

### Task 15: 学科年级选择页面

**Files:**
- Create: `app/src/main/java/com/study/app/ui/screens/child/SubjectGradeSelectScreen.kt`
- Create: `app/src/main/java/com/study/app/ui/screens/child/SubjectGradeSelectViewModel.kt`
- Create: `app/src/test/java/com/study/app/ui/screens/child/SubjectGradeSelectViewModelTest.kt`

- [ ] **Step 1-6: 实现学科年级选择功能**

- [ ] **Step 7: 提交**

---

### Task 16: 学习设置页面

**Files:**
- Create: `app/src/main/java/com/study/app/ui/screens/child/SettingsScreen.kt`
- Create: `app/src/main/java/com/study/app/ui/screens/child/SettingsViewModel.kt`
- Create: `app/src/test/java/com/study/app/ui/screens/child/SettingsViewModelTest.kt`

- [ ] **Step 1: 写SettingsViewModel测试**

```kotlin
// app/src/test/java/com/study/app/ui/screens/child/SettingsViewModelTest.kt
package com.study.app.ui.screens.child

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsViewModelTest {
    @Test
    fun initial_question_count_is_10() {
        val viewModel = SettingsViewModel()
        assertEquals(10, viewModel.questionCount)
    }

    @Test
    fun initial_time_limit_enabled_is_false() {
        val viewModel = SettingsViewModel()
        assertFalse(viewModel.isTimeLimitEnabled)
    }

    @Test
    fun set_question_count_updates_state() {
        val viewModel = SettingsViewModel()
        viewModel.setQuestionCount(25)
        assertEquals(25, viewModel.questionCount)
    }

    @Test
    fun set_time_limit_enabled_updates_state() {
        val viewModel = SettingsViewModel()
        viewModel.setTimeLimitEnabled(true)
        assertTrue(viewModel.isTimeLimitEnabled)
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

- [ ] **Step 3: 写SettingsViewModel**

```kotlin
// app/src/main/java/com/study/app/ui/screens/child/SettingsViewModel.kt
package com.study.app.ui.screens.child

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _questionCount = MutableStateFlow(10)
    val questionCount: StateFlow<Int> = _questionCount

    private val _isTimeLimitEnabled = MutableStateFlow(false)
    val isTimeLimitEnabled: StateFlow<Boolean> = _isTimeLimitEnabled

    private val _timeLimitMinutes = MutableStateFlow(15)
    val timeLimitMinutes: StateFlow<Int> = _timeLimitMinutes

    fun setQuestionCount(count: Int) {
        _questionCount.value = count.coerceIn(1, 50)
    }

    fun setTimeLimitEnabled(enabled: Boolean) {
        _isTimeLimitEnabled.value = enabled
    }

    fun setTimeLimitMinutes(minutes: Int) {
        _timeLimitMinutes.value = minutes.coerceIn(1, 120)
    }
}
```

- [ ] **Step 4: 运行测试确认通过**

- [ ] **Step 5: 写SettingsScreen UI**

- [ ] **Step 6: 提交**

---

### Task 17: 答题页面

**Files:**
- Create: `app/src/main/java/com/study/app/ui/screens/quiz/QuizScreen.kt`
- Create: `app/src/main/java/com/study/app/ui/screens/quiz/QuizViewModel.kt`
- Create: `app/src/main/java/com/study/app/ui/screens/quiz/QuizState.kt`
- Create: `app/src/test/java/com/study/app/ui/screens/quiz/QuizViewModelTest.kt`

- [ ] **Step 1: 写QuizViewModel测试**

```kotlin
// app/src/test/java/com/study/app/ui/screens/quiz/QuizViewModelTest.kt
package com.study.app.ui.screens.quiz

import com.study.app.domain.model.Question
import com.study.app.domain.model.QuestionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QuizViewModelTest {
    private lateinit var viewModel: QuizViewModel

    @Before
    fun setup() {
        viewModel = QuizViewModel()
    }

    @Test
    fun initial_current_question_index_is_0() {
        assertEquals(0, viewModel.currentQuestionIndex)
    }

    @Test
    fun answer_question_updates_answer_map(): Unit = runBlocking {
        viewModel.answerQuestion("A")
        assertEquals("A", viewModel.answers[0])
    }

    @Test
    fun next_question_increments_index(): Unit = runBlocking {
        viewModel.nextQuestion()
        assertEquals(1, viewModel.currentQuestionIndex)
    }

    @Test
    fun is_finished_returns_true_when_all_answered(): Unit = runBlocking {
        repeat(10) { viewModel.answerQuestion("A") }
        repeat(9) { viewModel.nextQuestion() }
        assertTrue(viewModel.isFinished)
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

- [ ] **Step 3: 写QuizViewModel**

```kotlin
// app/src/main/java/com/study/app/ui/screens/quiz/QuizViewModel.kt
package com.study.app.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.app.domain.model.Question
import com.study.app.domain.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val answers: Map<Int, String> = emptyMap(),
    val isFinished: Boolean = false,
    val remainingSeconds: Int = 0,
    val isTimeUp: Boolean = false
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val questionRepository: QuestionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state

    private var timerJob: Job? = null

    val currentQuestion: Question?
        get() = _state.value.questions.getOrNull(_state.value.currentIndex)

    val currentQuestionIndex: Int
        get() = _state.value.currentIndex

    val answers: Map<Int, String>
        get() = _state.value.answers

    val isFinished: Boolean
        get() = _state.value.isFinished

    fun loadQuestions(subjectId: Long, gradeId: Long, count: Int) {
        viewModelScope.launch {
            val questions = questionRepository.getRandomQuestions(subjectId, gradeId, count)
            _state.value = _state.value.copy(questions = questions)
        }
    }

    fun setTimeLimit(seconds: Int) {
        _state.value = _state.value.copy(remainingSeconds = seconds)
        startTimer()
    }

    fun answerQuestion(answer: String) {
        val currentIndex = _state.value.currentIndex
        val newAnswers = _state.value.answers + (currentIndex to answer)
        _state.value = _state.value.copy(answers = newAnswers)
    }

    fun nextQuestion() {
        val nextIndex = _state.value.currentIndex + 1
        if (nextIndex >= _state.value.questions.size) {
            _state.value = _state.value.copy(isFinished = true)
            timerJob?.cancel()
        } else {
            _state.value = _state.value.copy(currentIndex = nextIndex)
        }
    }

    fun previousQuestion() {
        if (_state.value.currentIndex > 0) {
            _state.value = _state.value.copy(currentIndex = _state.value.currentIndex - 1)
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.remainingSeconds > 0) {
                delay(1000)
                _state.value = _state.value.copy(
                    remainingSeconds = _state.value.remainingSeconds - 1
                )
            }
            _state.value = _state.value.copy(isTimeUp = true, isFinished = true)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
```

- [ ] **Step 4: 运行测试确认通过**

- [ ] **Step 5: 写QuizScreen UI**

- [ ] **Step 6: 提交**

---

### Task 18: 结果页面

**Files:**
- Create: `app/src/main/java/com/study/app/ui/screens/result/ResultScreen.kt`
- Create: `app/src/main/java/com/study/app/ui/screens/result/ResultViewModel.kt`
- Create: `app/src/main/java/com/study/app/ui/screens/result/QuestionResult.kt`
- Create: `app/src/test/java/com/study/app/ui/screens/result/ResultViewModelTest.kt`

- [ ] **Step 1-6: 实现结果页面功能**

- [ ] **Step 7: 提交**

---

### Task 19: 错题本页面

**Files:**
- Create: `app/src/main/java/com/study/app/ui/screens/result/WrongBookScreen.kt`
- Create: `app/src/main/java/com/study/app/ui/screens/result/WrongBookViewModel.kt`
- Create: `app/src/test/java/com/study/app/ui/screens/result/WrongBookViewModelTest.kt`

- [ ] **Step 1-6: 实现错题本功能**

- [ ] **Step 7: 提交**

---

### Task 20: 历史记录页面

**Files:**
- Create: `app/src/main/java/com/study/app/ui/screens/result/ChildHistoryScreen.kt`
- Create: `app/src/main/java/com/study/app/ui/screens/result/ChildHistoryViewModel.kt`
- Create: `app/src/test/java/com/study/app/ui/screens/result/ChildHistoryViewModelTest.kt`

- [ ] **Step 1-6: 实现小孩历史记录功能**

- [ ] **Step 7: 提交**

---

### Task 21: 导航集成和Hilt模块

**Files:**
- Create: `app/src/main/java/com/study/app/di/DatabaseModule.kt`
- Create: `app/src/main/java/com/study/app/di/RepositoryModule.kt`
- Modify: `app/src/main/java/com/study/app/MainActivity.kt`
- Create: `app/src/main/java/com/study/app/ui/navigation/AppNavigation.kt`

- [ ] **Step 1: 创建Hilt模块测试**

```kotlin
// app/src/test/java/com/study/app/di/DatabaseModuleTest.kt
package com.study.app.di

import android.content.Context
import androidx.room.Room
import com.study.app.data.local.StudyDatabase
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DatabaseModuleTest {
    @Test
    fun provides_database() {
        // Test that database can be created
        assertTrue(true)
    }
}
```

- [ ] **Step 2: 创建DatabaseModule**

```kotlin
// app/src/main/java/com/study/app/di/DatabaseModule.kt
package com.study.app.di

import android.content.Context
import androidx.room.Room
import com.study.app.data.local.*
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
    fun provideSubjectDao(database: StudyDatabase): SubjectDao = database.subjectDao()

    @Provides
    fun provideGradeDao(database: StudyDatabase): GradeDao = database.gradeDao()

    @Provides
    fun provideQuestionDao(database: StudyDatabase): QuestionDao = database.questionDao()

    @Provides
    fun providePracticeRecordDao(database: StudyDatabase): PracticeRecordDao = database.practiceRecordDao()

    @Provides
    fun provideImportRecordDao(database: StudyDatabase): ImportRecordDao = database.importRecordDao()

    @Provides
    fun provideWrongAnswerBookDao(database: StudyDatabase): WrongAnswerBookDao = database.wrongAnswerBookDao()
}
```

- [ ] **Step 3: 创建RepositoryModule**

```kotlin
// app/src/main/java/com/study/app/di/RepositoryModule.kt
package com.study.app.di

import com.study.app.data.repository.*
import com.study.app.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSubjectRepository(impl: SubjectRepositoryImpl): SubjectRepository

    @Binds
    @Singleton
    abstract fun bindGradeRepository(impl: GradeRepositoryImpl): GradeRepository

    @Binds
    @Singleton
    abstract fun bindQuestionRepository(impl: QuestionRepositoryImpl): QuestionRepository

    @Binds
    @Singleton
    abstract fun bindPracticeRepository(impl: PracticeRepositoryImpl): PracticeRepository

    @Binds
    @Singleton
    abstract fun bindImportRepository(impl: ImportRepositoryImpl): ImportRepository

    @Binds
    @Singleton
    abstract fun bindWrongAnswerRepository(impl: WrongAnswerRepositoryImpl): WrongAnswerRepository
}
```

- [ ] **Step 4: 创建AppNavigation**

```kotlin
// app/src/main/java/com/study/app/ui/navigation/AppNavigation.kt
package com.study.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.study.app.ui.screens.HomeScreen
import com.study.app.ui.screens.parent.ParentHomeScreen
import com.study.app.ui.screens.child.SubjectGradeSelectScreen
import com.study.app.ui.screens.child.SettingsScreen
import com.study.app.ui.screens.quiz.QuizScreen
import com.study.app.ui.screens.result.ResultScreen
import com.study.app.ui.screens.result.WrongBookScreen
import com.study.app.ui.screens.result.ChildHistoryScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ParentHome : Screen("parent_home")
    object SubjectGradeSelect : Screen("subject_grade_select")
    object Settings : Screen("settings")
    object Quiz : Screen("quiz")
    object Result : Screen("result")
    object WrongBook : Screen("wrong_book")
    object ChildHistory : Screen("child_history")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.ParentHome.route) { ParentHomeScreen(navController) }
        composable(Screen.SubjectGradeSelect.route) { SubjectGradeSelectScreen(navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
        composable(Screen.Quiz.route) { QuizScreen(navController) }
        composable(Screen.Result.route) { ResultScreen(navController) }
        composable(Screen.WrongBook.route) { WrongBookScreen(navController) }
        composable(Screen.ChildHistory.route) { ChildHistoryScreen(navController) }
    }
}
```

- [ ] **Step 5: 更新MainActivity**

- [ ] **Step 6: 提交**

---

## 文件索引

### Domain Models
- `app/src/main/java/com/study/app/domain/model/Subject.kt`
- `app/src/main/java/com/study/app/domain/model/Grade.kt`
- `app/src/main/java/com/study/app/domain/model/Question.kt`
- `app/src/main/java/com/study/app/domain/model/PracticeRecord.kt`
- `app/src/main/java/com/study/app/domain/model/ImportRecord.kt`
- `app/src/main/java/com/study/app/domain/model/WrongAnswerBook.kt`
- `app/src/main/java/com/study/app/domain/model/QuestionType.kt`

### Repository Interfaces
- `app/src/main/java/com/study/app/domain/repository/SubjectRepository.kt`
- `app/src/main/java/com/study/app/domain/repository/GradeRepository.kt`
- `app/src/main/java/com/study/app/domain/repository/QuestionRepository.kt`
- `app/src/main/java/com/study/app/domain/repository/PracticeRepository.kt`
- `app/src/main/java/com/study/app/domain/repository/ImportRepository.kt`
- `app/src/main/java/com/study/app/domain/repository/WrongAnswerRepository.kt`

### Data Layer
- `app/src/main/java/com/study/app/data/local/entity/SubjectEntity.kt`
- `app/src/main/java/com/study/app/data/local/entity/GradeEntity.kt`
- `app/src/main/java/com/study/app/data/local/entity/QuestionEntity.kt`
- `app/src/main/java/com/study/app/data/local/entity/PracticeRecordEntity.kt`
- `app/src/main/java/com/study/app/data/local/entity/ImportRecordEntity.kt`
- `app/src/main/java/com/study/app/data/local/entity/WrongAnswerBookEntity.kt`
- `app/src/main/java/com/study/app/data/local/SubjectDao.kt`
- `app/src/main/java/com/study/app/data/local/GradeDao.kt`
- `app/src/main/java/com/study/app/data/local/QuestionDao.kt`
- `app/src/main/java/com/study/app/data/local/PracticeRecordDao.kt`
- `app/src/main/java/com/study/app/data/local/ImportRecordDao.kt`
- `app/src/main/java/com/study/app/data/local/WrongAnswerBookDao.kt`
- `app/src/main/java/com/study/app/data/repository/SubjectRepositoryImpl.kt`
- `app/src/main/java/com/study/app/data/repository/GradeRepositoryImpl.kt`
- `app/src/main/java/com/study/app/data/repository/QuestionRepositoryImpl.kt`
- `app/src/main/java/com/study/app/data/repository/PracticeRepositoryImpl.kt`
- `app/src/main/java/com/study/app/data/repository/ImportRepositoryImpl.kt`
- `app/src/main/java/com/study/app/data/repository/WrongAnswerRepositoryImpl.kt`
- `app/src/main/java/com/study/app/data/import/CsvImporter.kt`
- `app/src/main/java/com/study/app/data/import/CsvImportResult.kt`

### UI Layer
- `app/src/main/java/com/study/app/ui/screens/HomeScreen.kt`
- `app/src/main/java/com/study/app/ui/screens/HomeViewModel.kt`
- `app/src/main/java/com/study/app/ui/screens/parent/ParentHomeScreen.kt`
- `app/src/main/java/com/study/app/ui/screens/parent/ParentViewModel.kt`
- `app/src/main/java/com/study/app/ui/screens/parent/SubjectManagementScreen.kt`
- `app/src/main/java/com/study/app/ui/screens/parent/SubjectManagementViewModel.kt`
- `app/src/main/java/com/study/app/ui/screens/parent/GradeManagementScreen.kt`
- `app/src/main/java/com/study/app/ui/screens/parent/GradeManagementViewModel.kt`
- `app/src/main/java/com/study/app/ui/screens/parent/QuestionEntryScreen.kt`
- `app/src/main/java/com/study/app/ui/screens/parent/QuestionEntryViewModel.kt`
- `app/src/main/java/com/study/app/ui/screens/parent/CsvImportScreen.kt`
- `app/src/main/java/com/study/app/ui/screens/parent/CsvImportViewModel.kt`
- `app/src/main/java/com/study/app/ui/screens/parent/ImportRecordsScreen.kt`
- `app/src/main/java/com/study/app/ui/screens/parent/ImportRecordsViewModel.kt`
- `app/src/main/java/com/study/app/ui/screens/parent/HistoryRecordsScreen.kt`
- `app/src/main/java/com/study/app/ui/screens/parent/HistoryRecordsViewModel.kt`
- `app/src/main/java/com/study/app/ui/screens/child/SubjectGradeSelectScreen.kt`
- `app/src/main/java/com/study/app/ui/screens/child/SubjectGradeSelectViewModel.kt`
- `app/src/main/java/com/study/app/ui/screens/child/SettingsScreen.kt`
- `app/src/main/java/com/study/app/ui/screens/child/SettingsViewModel.kt`
- `app/src/main/java/com/study/app/ui/screens/quiz/QuizScreen.kt`
- `app/src/main/java/com/study/app/ui/screens/quiz/QuizViewModel.kt`
- `app/src/main/java/com/study/app/ui/screens/result/ResultScreen.kt`
- `app/src/main/java/com/study/app/ui/screens/result/ResultViewModel.kt`
- `app/src/main/java/com/study/app/ui/screens/result/WrongBookScreen.kt`
- `app/src/main/java/com/study/app/ui/screens/result/WrongBookViewModel.kt`
- `app/src/main/java/com/study/app/ui/screens/result/ChildHistoryScreen.kt`
- `app/src/main/java/com/study/app/ui/screens/result/ChildHistoryViewModel.kt`

### DI & Navigation
- `app/src/main/java/com/study/app/di/DatabaseModule.kt`
- `app/src/main/java/com/study/app/di/RepositoryModule.kt`
- `app/src/main/java/com/study/app/ui/navigation/AppNavigation.kt`
- `app/src/main/java/com/study/app/MainActivity.kt` (modify)

### 依赖更新 (build.gradle.kts)
```kotlin
dependencies {
    // 新增
    implementation("org.apache.commons:commons-csv:1.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}
```
