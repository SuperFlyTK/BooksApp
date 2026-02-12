package com.example.booksapp.data.firebase.model

data class FirebaseFavorite(
    val bookId: String = "",
    val title: String = "",
    val coverUrl: String = "",
    val updatedAt: Long = 0L,
)
