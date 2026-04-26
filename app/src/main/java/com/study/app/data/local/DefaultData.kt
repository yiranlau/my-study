package com.study.app.data.local

import com.study.app.data.local.entity.GradeEntity
import com.study.app.data.local.entity.SubjectEntity

object DefaultData {
    val subjects = listOf(
        SubjectEntity(name = "语文", isDefault = true),
        SubjectEntity(name = "数学", isDefault = true),
        SubjectEntity(name = "英语", isDefault = true)
    )

    val grades = listOf(
        GradeEntity(name = "一年级", order = 1),
        GradeEntity(name = "二年级", order = 2),
        GradeEntity(name = "三年级", order = 3),
        GradeEntity(name = "四年级", order = 4),
        GradeEntity(name = "五年级", order = 5),
        GradeEntity(name = "六年级", order = 6)
    )
}
