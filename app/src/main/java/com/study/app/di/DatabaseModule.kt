package com.study.app.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.study.app.data.local.FlashcardDao
import com.study.app.data.local.GradeDao
import com.study.app.data.local.ImportRecordDao
import com.study.app.data.local.PracticeRecordDao
import com.study.app.data.local.QuestionDao
import com.study.app.data.local.SessionDao
import com.study.app.data.local.StudyDatabase
import com.study.app.data.local.SubjectDao
import com.study.app.data.local.WrongAnswerBookDao
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

    @Provides
    @Singleton
    fun provideSubjectDao(database: StudyDatabase): SubjectDao {
        return database.subjectDao()
    }

    @Provides
    @Singleton
    fun provideGradeDao(database: StudyDatabase): GradeDao {
        return database.gradeDao()
    }

    @Provides
    @Singleton
    fun provideQuestionDao(database: StudyDatabase): QuestionDao {
        return database.questionDao()
    }

    @Provides
    @Singleton
    fun provideImportRecordDao(database: StudyDatabase): ImportRecordDao {
        return database.importRecordDao()
    }

    @Provides
    @Singleton
    fun providePracticeRecordDao(database: StudyDatabase): PracticeRecordDao {
        return database.practiceRecordDao()
    }

    @Provides
    @Singleton
    fun provideWrongAnswerBookDao(database: StudyDatabase): WrongAnswerBookDao {
        return database.wrongAnswerBookDao()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("study_prefs", Context.MODE_PRIVATE)
    }
}
