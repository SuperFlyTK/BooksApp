package com.example.booksapp.domain.repository

import com.example.booksapp.domain.model.BookComment
import kotlinx.coroutines.flow.Flow

interface CommentsRepository {
    fun observeComments(bookId: String): Flow<List<BookComment>>

    suspend fun addComment(
        bookId: String,
        userId: String,
        userName: String,
        text: String,
        rating: Int,
    ): Result<Unit>

    suspend fun updateComment(
        bookId: String,
        commentId: String,
        userId: String,
        text: String,
        rating: Int,
    ): Result<Unit>

    suspend fun deleteComment(
        bookId: String,
        commentId: String,
        userId: String,
    ): Result<Unit>
}
