package com.example.booksapp.core.common

import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException

class RetryPolicy(
    private val maxAttempts: Int = 3,
    private val baseDelayMs: Long = 300L,
) {
    fun shouldRetry(attempt: Int, throwable: Throwable): Boolean {
        if (attempt >= maxAttempts) return false
        return when (throwable) {
            is IOException -> true
            is HttpException -> throwable.code() in 500..599
            else -> false
        }
    }

    suspend fun <T> run(block: suspend () -> T): T {
        var attempt = 1
        while (true) {
            try {
                return block()
            } catch (throwable: Throwable) {
                if (!shouldRetry(attempt, throwable)) throw throwable
                delay(baseDelayMs * attempt)
                attempt++
            }
        }
    }
}
