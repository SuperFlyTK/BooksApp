package com.example.booksapp.core.logging

import android.util.Log

object AppLogger {
    private var verboseEnabled: Boolean = true

    fun setVerbose(enabled: Boolean) {
        verboseEnabled = enabled
    }

    fun d(tag: String, message: String) {
        if (verboseEnabled) Log.d(tag, message)
    }

    fun i(tag: String, message: String) {
        Log.i(tag, message)
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        Log.w(tag, message, throwable)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
    }
}
