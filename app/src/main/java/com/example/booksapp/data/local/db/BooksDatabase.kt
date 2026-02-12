package com.example.booksapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.booksapp.data.local.dao.BooksDao
import com.example.booksapp.data.local.entity.BookEntity
import com.example.booksapp.data.local.entity.BookQueryEntity

@Database(
    entities = [BookEntity::class, BookQueryEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class BooksDatabase : RoomDatabase() {
    abstract fun booksDao(): BooksDao
}
