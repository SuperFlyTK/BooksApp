package com.example.booksapp.domain.usecase

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class QuerySanitizerTest {
    private val sanitizer = QuerySanitizer()

    @Test
    fun `sanitizes special characters`() {
        val cleaned = sanitizer.sanitize("Harry!@# Potter  ")
        assertThat(cleaned).isEqualTo("Harry Potter")
    }

    @Test
    fun `truncates long strings`() {
        val long = "a".repeat(120)
        val cleaned = sanitizer.sanitize(long)
        assertThat(cleaned.length).isAtMost(80)
    }

    @Test
    fun `keeps letters digits spaces apostrophes and hyphens`() {
        val cleaned = sanitizer.sanitize("  O'Connor - 1984  ")
        assertThat(cleaned).isEqualTo("O'Connor - 1984")
    }
}
