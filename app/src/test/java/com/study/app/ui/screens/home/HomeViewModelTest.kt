package com.study.app.ui.screens.home

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        viewModel = HomeViewModel()
    }

    @Test
    fun setParentMode_enablesParentMode() {
        assertFalse(viewModel.isParentMode)

        viewModel.setParentMode(true)

        assertTrue(viewModel.isParentMode)
    }

    @Test
    fun exitParentMode_resetsParentMode() {
        viewModel.setParentMode(true)
        assertTrue(viewModel.isParentMode)

        viewModel.exitParentMode()

        assertFalse(viewModel.isParentMode)
    }
}