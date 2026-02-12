package com.example.booksapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val coverUrl: String?,
    val firstPublishYear: Int?,
    val subjects: String,
    val description: String?,
    val ratingScore: Double,
    val lastUpdatedAt: Long,
)
