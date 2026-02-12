package com.example.booksapp.domain.repository

import com.example.booksapp.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BooksRepository {
    fun observeFeedBooks(): Flow<List<Book>>
    fun observeSearchBooks(query: String): Flow<List<Book>>
    fun observeBookById(bookId: String): Flow<Book?>

    suspend fun refreshFeed(force: Boolean = false): Result<Unit>
    suspend fun loadMoreFeed(): Result<Unit>
    suspend fun refreshSearch(query: String): Result<Unit>
    suspend fun loadMoreSearch(query: String): Result<Unit>
}
