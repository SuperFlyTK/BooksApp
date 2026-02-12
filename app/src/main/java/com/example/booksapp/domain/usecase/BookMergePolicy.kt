package com.example.booksapp.domain.usecase

import com.example.booksapp.domain.model.Book

class BookMergePolicy {
    fun merge(cached: List<Book>, remote: List<Book>): List<Book> {
        val merged = LinkedHashMap<String, Book>()
        cached.forEach { merged[it.id] = it }
        remote.forEach { incoming ->
            val existing = merged[incoming.id]
            merged[incoming.id] = if (existing == null || incoming.lastUpdatedAt >= existing.lastUpdatedAt) {
                incoming
            } else {
                existing
            }
        }
        return merged.values.toList()
    }
}
