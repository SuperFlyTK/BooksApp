package com.example.booksapp.domain.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String?,
    val firstPublishYear: Int?,
    val subjects: List<String>,
    val description: String?,
    val ratingScore: Double,
    val lastUpdatedAt: Long,
)
