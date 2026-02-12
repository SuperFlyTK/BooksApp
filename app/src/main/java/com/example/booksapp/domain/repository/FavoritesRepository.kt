package com.example.booksapp.domain.repository

import com.example.booksapp.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeFavoriteIds(userId: String): Flow<Set<String>>
    suspend fun toggleFavorite(userId: String, book: Book): Result<Unit>
}
