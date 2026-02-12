package com.example.booksapp.di

import android.content.Context
import com.example.booksapp.BuildConfig
import com.example.booksapp.core.logging.AppLogger
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebaseApp(@ApplicationContext context: Context): FirebaseApp? {
        val existing = FirebaseApp.getApps(context).firstOrNull()
        if (existing != null) return existing
        if (
            BuildConfig.FIREBASE_API_KEY.isBlank() ||
            BuildConfig.FIREBASE_APP_ID.isBlank() ||
            BuildConfig.FIREBASE_PROJECT_ID.isBlank() ||
            BuildConfig.FIREBASE_DATABASE_URL.isBlank()
        ) {
            return null
        }
        return FirebaseApp.initializeApp(
            context,
            FirebaseOptions.Builder()
                .setApiKey(BuildConfig.FIREBASE_API_KEY)
                .setApplicationId(BuildConfig.FIREBASE_APP_ID)
                .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
                .setDatabaseUrl(BuildConfig.FIREBASE_DATABASE_URL)
                .build(),
        )
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(firebaseApp: FirebaseApp?): FirebaseAuth? {
        return firebaseApp?.let { FirebaseAuth.getInstance(it) }
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(firebaseApp: FirebaseApp?): FirebaseDatabase? {
        return firebaseApp?.let {
            FirebaseDatabase.getInstance(it).apply {
                try {
                    setPersistenceEnabled(true)
                } catch (_: Exception) {
                    AppLogger.w("FirebaseModule", "Persistence was already configured")
                }
            }
        }
    }
}
