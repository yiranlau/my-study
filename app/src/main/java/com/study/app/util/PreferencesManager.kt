package com.study.app.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var lastSubjectId: Long
        get() = prefs.getLong(KEY_LAST_SUBJECT_ID, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_SUBJECT_ID, value).apply()

    var lastGradeId: Long
        get() = prefs.getLong(KEY_LAST_GRADE_ID, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_GRADE_ID, value).apply()

    companion object {
        private const val PREFS_NAME = "study_app_prefs"
        private const val KEY_LAST_SUBJECT_ID = "last_subject_id"
        private const val KEY_LAST_GRADE_ID = "last_grade_id"
    }
}
