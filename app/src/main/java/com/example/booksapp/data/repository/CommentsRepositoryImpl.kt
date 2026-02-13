package com.example.booksapp.data.repository

import com.example.booksapp.core.logging.AppLogger
import com.example.booksapp.data.firebase.model.FirebaseComment
import com.example.booksapp.domain.model.BookComment
import com.example.booksapp.domain.repository.CommentsRepository
import com.example.booksapp.domain.usecase.QuerySanitizer
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
class CommentsRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase?,
    private val querySanitizer: QuerySanitizer,
) : CommentsRepository {

    override fun observeComments(bookId: String): Flow<List<BookComment>> {
        val database = firebaseDatabase ?: return flowOf(emptyList())
        val safeBookId = sanitizeNodeKey(bookId)
        val ref = database.reference.child("comments").child(safeBookId)
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = snapshot.children.mapNotNull { child ->
                        child.getValue(FirebaseComment::class.java)?.toDomain()
                    }.sortedByDescending { it.updatedAt }
                    trySend(items)
                }

                override fun onCancelled(error: DatabaseError) {
                    AppLogger.w(TAG, "Comments listener cancelled for book=$safeBookId", error.toException())
                    trySend(emptyList())
                    close()
                }
            }
            ref.addValueEventListener(listener)
            awaitClose { ref.removeEventListener(listener) }
        }
    }

    override suspend fun addComment(
        bookId: String,
        userId: String,
        userName: String,
        text: String,
        rating: Int,
    ): Result<Unit> {
        val database = firebaseDatabase ?: return Result.failure(IllegalStateException("Firebase not configured"))
        return runCatching {
            val safeBookId = sanitizeNodeKey(bookId)
            val commentsRef = database.reference.child("comments").child(safeBookId)
            val id = commentsRef.push().key ?: error("Unable to create comment key")
            commentsRef.child(id).setValue(
                FirebaseComment(
                    id = id,
                    bookId = safeBookId,
                    userId = userId,
                    userName = userName.trim().ifBlank { "Anonymous" },
                    text = sanitizeReviewText(text),
                    rating = rating,
                    updatedAt = System.currentTimeMillis(),
                ),
            ).await()
            Unit
        }.onFailure { throwable ->
            AppLogger.w(TAG, "Comment add failed for book=$bookId", throwable)
        }
    }

    override suspend fun updateComment(
        bookId: String,
        commentId: String,
        userId: String,
        text: String,
        rating: Int,
    ): Result<Unit> {
        val database = firebaseDatabase ?: return Result.failure(IllegalStateException("Firebase not configured"))
        return runCatching {
            val safeBookId = sanitizeNodeKey(bookId)
            val ref = database.reference.child("comments").child(safeBookId).child(commentId)
            val existing = ref.get().await().getValue(FirebaseComment::class.java)
            if (existing == null || existing.userId != userId) {
                error("Cannot edit someone else's comment")
            }
            ref.setValue(
                existing.copy(
                    text = sanitizeReviewText(text),
                    rating = rating,
                    updatedAt = System.currentTimeMillis(),
                ),
            ).await()
            Unit
        }.onFailure { throwable ->
            AppLogger.w(TAG, "Comment update failed for book=$bookId comment=$commentId", throwable)
        }
    }

    override suspend fun deleteComment(bookId: String, commentId: String, userId: String): Result<Unit> {
        val database = firebaseDatabase ?: return Result.failure(IllegalStateException("Firebase not configured"))
        return runCatching {
            val safeBookId = sanitizeNodeKey(bookId)
            val ref = database.reference.child("comments").child(safeBookId).child(commentId)
            val existing = ref.get().await().getValue(FirebaseComment::class.java)
            if (existing == null || existing.userId != userId) {
                error("Cannot delete someone else's comment")
            }
            ref.removeValue().await()
            Unit
        }.onFailure { throwable ->
            AppLogger.w(TAG, "Comment delete failed for book=$bookId comment=$commentId", throwable)
        }
    }

    private fun sanitizeReviewText(input: String): String {
        return querySanitizer.sanitize(input).take(400)
    }

    private fun sanitizeNodeKey(input: String): String {
        val normalized = querySanitizer.sanitize(input)
            .replace(Regex("[.#$\\[\\]/]"), "_")
            .take(120)
        return normalized.ifBlank { "_" }
    }

    private companion object {
        const val TAG = "CommentsRepository"
    }
}

private fun FirebaseComment.toDomain(): BookComment {
    return BookComment(
        id = id,
        bookId = bookId,
        userId = userId,
        userName = userName,
        text = text,
        rating = rating,
        updatedAt = updatedAt,
    )
}
