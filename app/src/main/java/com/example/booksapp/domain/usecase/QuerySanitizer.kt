package com.example.booksapp.domain.usecase

class QuerySanitizer {
    fun sanitize(input: String): String {
        return input
            .trim()
            .replace(Regex("[^\\p{L}\\p{N}\\s'-]"), "")
            .replace(Regex("\\s+"), " ")
            .take(80)
    }
}
