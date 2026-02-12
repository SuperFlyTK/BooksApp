package com.example.booksapp.domain.usecase

import com.example.booksapp.domain.model.Book

class RecommendationScorer {
    fun score(
        book: Book,
        preferredSubjects: Set<String>,
        isFavorite: Boolean,
    ): Double {
        val subjectHits = book.subjects.count { preferredSubjects.contains(it.lowercase()) }
        val favoriteBonus = if (isFavorite) 1.2 else 0.0
        val recencyPenalty = if (book.firstPublishYear != null && book.firstPublishYear < 1980) 0.3 else 0.0
        return (book.ratingScore * 0.7) + (subjectHits * 0.5) + favoriteBonus - recencyPenalty
    }
}
