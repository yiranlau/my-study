package com.study.app.ui.screens.child

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var viewModel: SettingsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SettingsViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initial_question_count_is_10() {
        assertEquals(10, viewModel.questionCount.value)
    }

    @Test
    fun initial_time_limit_enabled_is_false() {
        assertFalse(viewModel.isTimeLimitEnabled.value)
    }

    @Test
    fun initial_time_limit_minutes_is_15() {
        assertEquals(15, viewModel.timeLimitMinutes.value)
    }

    @Test
    fun set_question_count_updates_value() = runTest {
        viewModel.setQuestionCount(25)
        assertEquals(25, viewModel.questionCount.value)
    }

    @Test
    fun set_question_count_above_50_is_clamped_to_50() = runTest {
        viewModel.setQuestionCount(100)
        assertEquals(50, viewModel.questionCount.value)
    }

    @Test
    fun set_question_count_below_1_is_clamped_to_1() = runTest {
        viewModel.setQuestionCount(-5)
        assertEquals(1, viewModel.questionCount.value)
    }

    @Test
    fun set_time_limit_enabled_updates_value() = runTest {
        viewModel.setTimeLimitEnabled(true)
        assertTrue(viewModel.isTimeLimitEnabled.value)
    }

    @Test
    fun set_time_limit_minutes_updates_value() = runTest {
        viewModel.setTimeLimitMinutes(30)
        assertEquals(30, viewModel.timeLimitMinutes.value)
    }

    @Test
    fun set_time_limit_minutes_above_120_is_clamped_to_120() = runTest {
        viewModel.setTimeLimitMinutes(200)
        assertEquals(120, viewModel.timeLimitMinutes.value)
    }

    @Test
    fun set_time_limit_minutes_below_1_is_clamped_to_1() = runTest {
        viewModel.setTimeLimitMinutes(0)
        assertEquals(1, viewModel.timeLimitMinutes.value)
    }
}
