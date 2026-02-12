package com.example.booksapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.booksapp.data.local.entity.BookEntity
import com.example.booksapp.data.local.entity.BookQueryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BooksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBooks(items: List<BookEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertQueryItems(items: List<BookQueryEntity>)

    @Query(
        """
        SELECT b.* FROM books b
        INNER JOIN book_queries q ON b.id = q.bookId
        WHERE q.queryTag = :queryTag
        ORDER BY q.position ASC
        """,
    )
    fun observeBooksForQuery(queryTag: String): Flow<List<BookEntity>>

    @Query(
        """
        SELECT b.* FROM books b
        INNER JOIN book_queries q ON b.id = q.bookId
        WHERE q.queryTag = :queryTag
        ORDER BY q.position ASC
        """,
    )
    suspend fun getBooksForQuery(queryTag: String): List<BookEntity>

    @Query("SELECT * FROM books WHERE id = :bookId LIMIT 1")
    fun observeBookById(bookId: String): Flow<BookEntity?>

    @Query("DELETE FROM book_queries WHERE queryTag = :queryTag")
    suspend fun clearQuery(queryTag: String)

    @Query("SELECT COALESCE(MAX(page), 0) FROM book_queries WHERE queryTag = :queryTag")
    suspend fun getLastLoadedPage(queryTag: String): Int

    @Query("SELECT COALESCE(MAX(fetchedAt), 0) FROM book_queries WHERE queryTag = :queryTag")
    suspend fun getLastFetchedAt(queryTag: String): Long

    @Transaction
    suspend fun replaceQuery(queryTag: String, books: List<BookEntity>, page: Int, fetchedAt: Long) {
        clearQuery(queryTag)
        upsertBooks(books)
        val mapped = books.mapIndexed { index, book ->
            BookQueryEntity(
                queryTag = queryTag,
                bookId = book.id,
                position = index,
                page = page,
                fetchedAt = fetchedAt,
            )
        }
        upsertQueryItems(mapped)
    }
}
