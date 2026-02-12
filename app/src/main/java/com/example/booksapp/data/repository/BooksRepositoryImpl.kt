package com.example.booksapp.data.repository

import com.example.booksapp.core.common.CachePolicy
import com.example.booksapp.core.common.DispatchersProvider
import com.example.booksapp.core.common.RetryPolicy
import com.example.booksapp.core.logging.AppLogger
import com.example.booksapp.data.local.dao.BooksDao
import com.example.booksapp.data.local.entity.BookQueryEntity
import com.example.booksapp.data.mapper.toDomain
import com.example.booksapp.data.mapper.toEntity
import com.example.booksapp.data.remote.api.OpenLibraryApi
import com.example.booksapp.domain.model.Book
import com.example.booksapp.domain.repository.BooksRepository
import com.example.booksapp.domain.usecase.BookMergePolicy
import com.example.booksapp.domain.usecase.QuerySanitizer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BooksRepositoryImpl @Inject constructor(
    private val api: OpenLibraryApi,
    private val booksDao: BooksDao,
    private val cachePolicy: CachePolicy,
    private val retryPolicy: RetryPolicy,
    private val querySanitizer: QuerySanitizer,
    private val mergePolicy: BookMergePolicy,
    private val dispatchers: DispatchersProvider,
) : BooksRepository {

    override fun observeFeedBooks(): Flow<List<Book>> {
        return booksDao.observeBooksForQuery(FEED_TAG).map { it.map { entity -> entity.toDomain() } }
    }

    override fun observeSearchBooks(query: String): Flow<List<Book>> {
        val tag = queryTag(query)
        return booksDao.observeBooksForQuery(tag).map { it.map { entity -> entity.toDomain() } }
    }

    override fun observeBookById(bookId: String): Flow<Book?> {
        return booksDao.observeBookById(bookId).map { entity -> entity?.toDomain() }
    }

    override suspend fun refreshFeed(force: Boolean): Result<Unit> {
        return withContext(dispatchers.io) {
            fetchQueryPage(
                query = FEED_QUERY,
                queryTag = FEED_TAG,
                page = 1,
                reset = true,
                force = force,
            )
        }
    }

    override suspend fun loadMoreFeed(): Result<Unit> {
        return withContext(dispatchers.io) {
            val nextPage = booksDao.getLastLoadedPage(FEED_TAG) + 1
            fetchQueryPage(
                query = FEED_QUERY,
                queryTag = FEED_TAG,
                page = if (nextPage <= 0) 1 else nextPage,
                reset = false,
                force = true,
            )
        }
    }

    override suspend fun refreshSearch(query: String): Result<Unit> {
        return withContext(dispatchers.io) {
            val sanitized = querySanitizer.sanitize(query)
            if (sanitized.isBlank()) return@withContext Result.success(Unit)
            fetchQueryPage(
                query = sanitized,
                queryTag = queryTag(sanitized),
                page = 1,
                reset = true,
                force = true,
            )
        }
    }

    override suspend fun loadMoreSearch(query: String): Result<Unit> {
        return withContext(dispatchers.io) {
            val sanitized = querySanitizer.sanitize(query)
            if (sanitized.isBlank()) return@withContext Result.success(Unit)
            val tag = queryTag(sanitized)
            val nextPage = booksDao.getLastLoadedPage(tag) + 1
            fetchQueryPage(
                query = sanitized,
                queryTag = tag,
                page = if (nextPage <= 0) 1 else nextPage,
                reset = false,
                force = true,
            )
        }
    }

    private suspend fun fetchQueryPage(
        query: String,
        queryTag: String,
        page: Int,
        reset: Boolean,
        force: Boolean,
    ): Result<Unit> {
        return runCatching {
            val lastFetchedAt = booksDao.getLastFetchedAt(queryTag)
            if (!force && !cachePolicy.isStale(lastFetchedAt)) {
                AppLogger.d(TAG, "Skip refresh for '$queryTag' because cache is still fresh")
                return@runCatching Unit
            }

            val now = System.currentTimeMillis()
            AppLogger.d(TAG, "Fetch query='$query' tag='$queryTag' page=$page reset=$reset")
            val response = retryPolicy.run {
                api.searchBooks(
                    query = query,
                    page = page,
                    limit = PAGE_SIZE,
                )
            }

            val incomingBooks = mergePolicy.merge(
                cached = emptyList(),
                remote = response.docs.mapNotNull { it.toEntity(now)?.toDomain() },
            )

            if (reset) {
                booksDao.replaceQuery(
                    queryTag = queryTag,
                    books = incomingBooks.map { it.toEntity() },
                    page = page,
                    fetchedAt = now,
                )
            } else {
                val existingBooks = booksDao.getBooksForQuery(queryTag).map { it.toDomain() }
                val existingIds = existingBooks.mapTo(mutableSetOf()) { it.id }
                val mergedBooks = mergePolicy.merge(existingBooks, incomingBooks)
                booksDao.upsertBooks(mergedBooks.map { it.toEntity() })

                val newBooksForQuery = incomingBooks.filterNot { it.id in existingIds }
                val start = booksDao.getBooksForQuery(queryTag).size
                val mapped = newBooksForQuery.mapIndexed { index, book ->
                    BookQueryEntity(
                        queryTag = queryTag,
                        bookId = book.id,
                        position = start + index,
                        page = page,
                        fetchedAt = now,
                    )
                }
                if (mapped.isNotEmpty()) {
                    booksDao.upsertQueryItems(mapped)
                }
            }
        }.recoverCatching { throwable ->
            val cached = booksDao.getBooksForQuery(queryTag)
            if (cached.isEmpty()) {
                AppLogger.e(TAG, "Remote fetch failed and cache is empty for '$queryTag'", throwable)
                throw throwable
            }
            AppLogger.w(TAG, "Remote fetch failed for '$queryTag', showing cached data", throwable)
        }
    }

    private fun queryTag(query: String): String = "search:${querySanitizer.sanitize(query).lowercase()}"

    private companion object {
        const val TAG = "BooksRepository"
        const val FEED_TAG = "__feed__"
        const val FEED_QUERY = "bestseller"
        const val PAGE_SIZE = 20
    }
}
