package com.example.booksapp.domain.usecase

import com.example.booksapp.domain.model.Book
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BookMergePolicyTest {
    private val policy = BookMergePolicy()

    @Test
    fun `prefers latest timestamp`() {
        val cached = listOf(
            Book("1", "Cached", "A", null, null, emptyList(), null, 4.0, 1000L),
        )
        val remote = listOf(
            Book("1", "Remote", "A", null, null, emptyList(), null, 4.5, 2000L),
        )
        val merged = policy.merge(cached, remote)
        assertThat(merged.single().title).isEqualTo("Remote")
    }

    @Test
    fun `keeps cached when remote is older`() {
        val cached = listOf(
            Book("1", "Cached", "A", null, null, emptyList(), null, 4.0, 2000L),
        )
        val remote = listOf(
            Book("1", "Remote older", "A", null, null, emptyList(), null, 4.5, 1000L),
        )
        val merged = policy.merge(cached, remote)
        assertThat(merged.single().title).isEqualTo("Cached")
    }

    @Test
    fun `adds new remote entities without dropping cached ones`() {
        val cached = listOf(
            Book("1", "Cached", "A", null, null, emptyList(), null, 4.0, 1000L),
        )
        val remote = listOf(
            Book("2", "New", "B", null, null, emptyList(), null, 4.7, 1000L),
        )
        val merged = policy.merge(cached, remote)
        assertThat(merged.map { it.id }).containsExactly("1", "2").inOrder()
    }
}
