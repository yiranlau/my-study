package com.study.app.di

import android.content.Context
import androidx.room.Room
import com.study.app.data.local.FlashcardDao
import com.study.app.data.local.SessionDao
import com.study.app.data.local.StudyDatabase
import com.study.app.data.repository.FlashcardRepositoryImpl
import com.study.app.data.repository.SessionRepositoryImpl
import com.study.app.domain.repository.FlashcardRepository
import com.study.app.domain.repository.SessionRepository
import dagger.Binds
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
    fun provideSessionRepository(impl: SessionRepositoryImpl): SessionRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideFlashcardRepository(impl: FlashcardRepositoryImpl): FlashcardRepository {
        return impl
    }
}