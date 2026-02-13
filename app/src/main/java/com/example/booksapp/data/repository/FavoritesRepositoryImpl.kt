package com.example.booksapp.data.repository

import com.example.booksapp.core.logging.AppLogger
import com.example.booksapp.data.firebase.model.FirebaseFavorite
import com.example.booksapp.domain.model.Book
import com.example.booksapp.domain.repository.FavoritesRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase?,
) : FavoritesRepository {

    override fun observeFavoriteIds(userId: String): Flow<Set<String>> {
        val database = firebaseDatabase ?: return flowOf(emptySet())
        val safeUserId = sanitizeNodeKey(userId)
        val ref = database.reference.child("users").child(safeUserId).child("favorites")
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val ids = snapshot.children.mapNotNull { it.key }.toSet()
                    trySend(ids)
                }

                override fun onCancelled(error: DatabaseError) {
                    AppLogger.w(TAG, "Favorites listener cancelled for user=$safeUserId", error.toException())
                    trySend(emptySet())
                    close()
                }
            }
            ref.addValueEventListener(listener)
            awaitClose { ref.removeEventListener(listener) }
        }
    }

    override suspend fun toggleFavorite(userId: String, book: Book): Result<Unit> {
        val database = firebaseDatabase ?: return Result.failure(IllegalStateException("Firebase not configured"))
        return runCatching {
            val safeUserId = sanitizeNodeKey(userId)
            val safeBookId = sanitizeNodeKey(book.id)
            val ref = database.reference.child("users").child(safeUserId).child("favorites").child(safeBookId)
            val existing = ref.get().await()
            if (existing.exists()) {
                ref.removeValue().await()
            } else {
                ref.setValue(
                    FirebaseFavorite(
                        bookId = safeBookId,
                        title = book.title,
                        coverUrl = book.coverUrl.orEmpty(),
                        updatedAt = System.currentTimeMillis(),
                    ),
                ).await()
            }
            Unit
        }.onFailure { throwable ->
            AppLogger.w(TAG, "Favorite toggle failed for user=$userId book=${book.id}", throwable)
        }
    }

    private fun sanitizeNodeKey(input: String): String {
        val normalized = input.trim()
            .replace(Regex("[.#$\\[\\]/]"), "_")
            .take(120)
        return normalized.ifBlank { "_" }
    }

    private companion object {
        const val TAG = "FavoritesRepository"
    }
}
