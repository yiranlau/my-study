package com.study.app.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
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
    fun sessionDao_exists() {
        assertNotNull(database.sessionDao())
    }

    @Test
    fun flashcardDao_exists() {
        assertNotNull(database.flashcardDao())
    }

    @Test
    fun subjectDao_exists() {
        assertNotNull(database.subjectDao())
    }

    @Test
    fun gradeDao_exists() {
        assertNotNull(database.gradeDao())
    }

    @Test
    fun questionDao_exists() {
        assertNotNull(database.questionDao())
    }

    @Test
    fun practiceRecordDao_exists() {
        assertNotNull(database.practiceRecordDao())
    }

    @Test
    fun importRecordDao_exists() {
        assertNotNull(database.importRecordDao())
    }

    @Test
    fun wrongAnswerBookDao_exists() {
        assertNotNull(database.wrongAnswerBookDao())
    }
}