package com.study.app.di

import com.study.app.data.repository.FlashcardRepositoryImpl
import com.study.app.data.repository.GradeRepositoryImpl
import com.study.app.data.repository.ImportRepositoryImpl
import com.study.app.data.repository.PracticeRepositoryImpl
import com.study.app.data.repository.QuestionRepositoryImpl
import com.study.app.data.repository.SessionRepositoryImpl
import com.study.app.data.repository.SubjectRepositoryImpl
import com.study.app.data.repository.WrongAnswerRepositoryImpl
import com.study.app.domain.repository.FlashcardRepository
import com.study.app.domain.repository.GradeRepository
import com.study.app.domain.repository.ImportRepository
import com.study.app.domain.repository.PracticeRepository
import com.study.app.domain.repository.QuestionRepository
import com.study.app.domain.repository.SessionRepository
import com.study.app.domain.repository.SubjectRepository
import com.study.app.domain.repository.WrongAnswerRepository
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
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

    @Binds
    @Singleton
    abstract fun bindFlashcardRepository(impl: FlashcardRepositoryImpl): FlashcardRepository

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
    abstract fun bindImportRepository(impl: ImportRepositoryImpl): ImportRepository

    @Binds
    @Singleton
    abstract fun bindPracticeRepository(impl: PracticeRepositoryImpl): PracticeRepository

    @Binds
    @Singleton
    abstract fun bindWrongAnswerRepository(impl: WrongAnswerRepositoryImpl): WrongAnswerRepository
}
