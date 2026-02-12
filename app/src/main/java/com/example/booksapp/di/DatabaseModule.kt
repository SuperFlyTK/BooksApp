package com.example.booksapp.di

import android.content.Context
import androidx.room.Room
import com.example.booksapp.data.local.dao.BooksDao
import com.example.booksapp.data.local.db.BooksDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object
DatabaseModule {
    @Provides
    @Singleton
    fun provideBooksDatabase(@ApplicationContext context: Context): BooksDatabase {
        return Room.databaseBuilder(
            context,
            BooksDatabase::class.java,
            "books_app.db",
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    @Provides
    @Singleton
    fun provideBooksDao(database: BooksDatabase): BooksDao = database.booksDao()
}
