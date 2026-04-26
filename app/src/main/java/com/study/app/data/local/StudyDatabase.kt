package com.study.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.study.app.data.local.entity.GradeEntity
import com.study.app.data.local.entity.ImportRecordEntity
import com.study.app.data.local.entity.PracticeRecordEntity
import com.study.app.data.local.entity.QuestionEntity
import com.study.app.data.local.entity.SubjectEntity
import com.study.app.data.local.entity.WrongAnswerBookEntity

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