package com.example.booksapp.domain.model

data class BookComment(
    val id: String,
    val bookId: String,
    val userId: String,
    val userName: String,
    val text: String,
    val rating: Int,
    val updatedAt: Long,
)
