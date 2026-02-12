package com.example.booksapp.domain.usecase

data class ReviewValidationResult(
    val isValid: Boolean,
    val textError: String? = null,
    val ratingError: String? = null,
)

class ReviewValidator {
    fun validate(text: String, rating: Int): ReviewValidationResult {
        val textError = if (text.trim().length < 10) "Review text must be at least 10 characters" else null
        val ratingError = if (rating !in 1..5) "Rating must be between 1 and 5" else null
        return ReviewValidationResult(
            isValid = textError == null && ratingError == null,
            textError = textError,
            ratingError = ratingError,
        )
    }
}
