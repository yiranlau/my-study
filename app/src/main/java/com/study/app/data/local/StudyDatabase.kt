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