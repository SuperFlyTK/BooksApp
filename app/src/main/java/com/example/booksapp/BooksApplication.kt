package com.example.booksapp

import android.app.Application
import com.example.booksapp.core.logging.AppLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BooksApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppLogger.setVerbose(BuildConfig.ENABLE_VERBOSE_LOGS)
        AppLogger.i("App", "BooksApp started in ${BuildConfig.ENVIRONMENT_NAME}")
    }
}
