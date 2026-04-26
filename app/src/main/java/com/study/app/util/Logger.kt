package com.study.app.util

import android.util.Log

/**
 * Logger utility that automatically skips logging in test environment.
 *
 * Usage:
 *   Logger.d(TAG, "message")
 *   Logger.e(TAG, "error", throwable)
 */
object Logger {
    private val isAndroid: Boolean by lazy {
        try {
            Class.forName("android.os.Build")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    fun d(tag: String, message: String) {
        if (isAndroid) {
            try {
                Log.d(tag, message)
            } catch (e: Throwable) {
                // Silently ignore in test environment
            }
        }
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (isAndroid) {
            try {
                Log.e(tag, message, throwable)
            } catch (e: Throwable) {
                // Silently ignore in test environment
            }
        }
    }

    fun i(tag: String, message: String) {
        if (isAndroid) {
            try {
                Log.i(tag, message)
            } catch (e: Throwable) {
                // Silently ignore in test environment
            }
        }
    }

    fun w(tag: String, message: String) {
        if (isAndroid) {
            try {
                Log.w(tag, message)
            } catch (e: Throwable) {
                // Silently ignore in test environment
            }
        }
    }
}
