package com.example.booksapp.core.common

class CachePolicy(
    private val staleAfterMs: Long = 15 * 60 * 1000L,
) {
    fun isStale(lastUpdatedAt: Long, now: Long = System.currentTimeMillis()): Boolean {
        if (lastUpdatedAt <= 0L) return true
        return now - lastUpdatedAt > staleAfterMs
    }
}
