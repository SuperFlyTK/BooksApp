package com.example.booksapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "book_queries",
    primaryKeys = ["queryTag", "bookId"],
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["queryTag"]),
        Index(value = ["bookId"]),
        Index(value = ["queryTag", "position"]),
    ],
)
data class BookQueryEntity(
    val queryTag: String,
    val bookId: String,
    val position: Int,
    val page: Int,
    val fetchedAt: Long,
)
