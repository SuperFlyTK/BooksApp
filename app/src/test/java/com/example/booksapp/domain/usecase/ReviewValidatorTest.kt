package com.example.booksapp.domain.usecase

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReviewValidatorTest {
    private val validator = ReviewValidator()

    @Test
    fun `valid review accepted`() {
        val result = validator.validate("Great book with solid pacing", 4)
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun `following negative cases fail`() {
        val shortTextResult = validator.validate("Bad", 2)
        assertThat(shortTextResult.textError).isNotNull()
        val wrongRating = validator.validate("Nice story", 6)
        assertThat(wrongRating.ratingError).isNotNull()
    }

    @Test
    fun `accepts boundary rating values`() {
        val oneStar = validator.validate("Long enough review text", 1)
        val fiveStars = validator.validate("Another valid review text", 5)
        assertThat(oneStar.isValid).isTrue()
        assertThat(fiveStars.isValid).isTrue()
    }
}
