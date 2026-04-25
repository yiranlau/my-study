package com.study.app.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Test

class ColorTest {
    @Test
    fun primary_color_exists() {
        // StudyPrimary color 应该被定义
        assertEquals("StudyPrimary", ::StudyPrimary.name)
    }
}