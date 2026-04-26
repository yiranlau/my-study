package com.study.app

import android.app.Application
import com.study.app.data.repository.GradeRepositoryImpl
import com.study.app.data.repository.SubjectRepositoryImpl
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class StudyApplication : Application() {

    @Inject
    lateinit var subjectRepository: SubjectRepositoryImpl

    @Inject
    lateinit var gradeRepository: GradeRepositoryImpl

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            subjectRepository.initializeDefaultData()
            gradeRepository.initializeDefaultData()
        }
    }
}
