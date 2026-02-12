package com.example.booksapp.data.firebase.model

data class FirebaseComment(
    val id: String = "",
    val bookId: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val rating: Int = 0,
    val updatedAt: Long = 0L,
)
