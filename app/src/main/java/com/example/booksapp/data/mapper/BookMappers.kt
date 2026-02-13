package com.example.booksapp.data.mapper

import com.example.booksapp.data.local.entity.BookEntity
import com.example.booksapp.data.remote.dto.BookDocDto
import com.example.booksapp.domain.model.Book

private const val SUBJECT_SEPARATOR = "|#|"

fun BookDocDto.toEntity(now: Long): BookEntity? {
    val rawKey = key ?: return null
    val normalizedId = rawKey.removePrefix("/works/").ifBlank { return null }
    val titleValue = title?.trim().orEmpty().ifBlank { return null }

    return BookEntity(
        id = normalizedId,
        title = titleValue,
        author = authorNames?.firstOrNull().orEmpty().ifBlank { "Unknown author" },
        coverUrl = when {
            coverId != null -> "https://covers.openlibrary.org/b/id/$coverId-M.jpg"
            !coverEditionKey.isNullOrBlank() -> {
                "https://covers.openlibrary.org/b/olid/${coverEditionKey.trim()}-M.jpg"
            }
            !isbn.isNullOrEmpty() -> {
                val safeIsbn = isbn.firstOrNull().orEmpty().trim()
                if (safeIsbn.isBlank()) null else "https://covers.openlibrary.org/b/isbn/$safeIsbn-M.jpg"
            }
            else -> null
        },
        firstPublishYear = firstPublishYear,
        subjects = subjects.orEmpty().joinToString(SUBJECT_SEPARATOR),
        description = null,
        ratingScore = ratingsAverage ?: 3.5,
        lastUpdatedAt = now,
    )
}

fun BookEntity.toDomain(): Book {
    return Book(
        id = id,
        title = title,
        author = author,
        coverUrl = coverUrl,
        firstPublishYear = firstPublishYear,
        subjects = if (subjects.isBlank()) emptyList() else subjects.split(SUBJECT_SEPARATOR),
        description = description,
        ratingScore = ratingScore,
        lastUpdatedAt = lastUpdatedAt,
    )
}

fun Book.toEntity(): BookEntity {
    return BookEntity(
        id = id,
        title = title,
        author = author,
        coverUrl = coverUrl,
        firstPublishYear = firstPublishYear,
        subjects = subjects.joinToString(SUBJECT_SEPARATOR),
        description = description,
        ratingScore = ratingScore,
        lastUpdatedAt = lastUpdatedAt,
    )
}
