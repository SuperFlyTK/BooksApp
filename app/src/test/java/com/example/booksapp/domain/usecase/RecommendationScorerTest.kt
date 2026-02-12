package com.example.booksapp.domain.usecase

import com.example.booksapp.domain.model.Book
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RecommendationScorerTest {
    private val scorer = RecommendationScorer()

    private val baseBook = Book(
        id = "1",
        title = "Any",
        author = "Author",
        coverUrl = null,
        firstPublishYear = 1995,
        subjects = listOf("fiction"),
        description = null,
        ratingScore = 4.0,
        lastUpdatedAt = 0L,
    )

    @Test
    fun `favorite boosts score`() {
        val baseScore = scorer.score(baseBook, setOf("fiction"), isFavorite = false)
        val favoriteScore = scorer.score(baseBook, setOf("fiction"), isFavorite = true)
        assertThat(favoriteScore).isGreaterThan(baseScore)
    }

    @Test
    fun `subject matches increase score`() {
        val noMatch = scorer.score(baseBook, setOf("history"), isFavorite = false)
        val match = scorer.score(baseBook, setOf("fiction"), isFavorite = false)
        assertThat(match).isGreaterThan(noMatch)
    }

    @Test
    fun `very old books get recency penalty`() {
        val oldBook = baseBook.copy(firstPublishYear = 1960)
        val oldScore = scorer.score(oldBook, setOf("fiction"), isFavorite = false)
        val modernScore = scorer.score(baseBook, setOf("fiction"), isFavorite = false)
        assertThat(oldScore).isLessThan(modernScore)
    }
}
