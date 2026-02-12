package com.example.booksapp.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.booksapp.domain.model.Book
import com.example.booksapp.ui.common.LoadState
import com.example.booksapp.ui.components.BookCard
import com.example.booksapp.ui.components.EmptyContent
import com.example.booksapp.ui.components.ErrorContent
import com.example.booksapp.ui.components.LoadingContent

@Composable
fun SearchScreen(
    snackbarHostState: SnackbarHostState,
    onBookClick: (Book) -> Unit,
    onToggleFavorite: (Book) -> Unit,
    favoriteIds: Set<String>,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(state.infoMessage) {
        val message = state.infoMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = viewModel::onQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            label = { Text("Search books") },
            placeholder = { Text("e.g. Clean Code") },
            singleLine = true,
        )

        when (val loadState = state.state) {
            LoadState.Idle -> EmptyContent(message = "Start typing to search")
            LoadState.Loading -> LoadingContent()
            LoadState.Empty -> EmptyContent(message = "No results found")
            is LoadState.Error -> ErrorContent(
                message = loadState.message,
                onRetry = viewModel::retryCurrentQuery,
            )
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    itemsIndexed(state.books, key = { _, book -> book.id }) { index, book ->
                        BookCard(
                            book = book,
                            isFavorite = favoriteIds.contains(book.id),
                            onClick = { onBookClick(book) },
                            onFavoriteClick = { onToggleFavorite(book) },
                        )
                        if (index >= state.books.lastIndex - 3 && !state.isLoadingMore) {
                            viewModel.loadMore()
                        }
                    }
                    if (state.isLoadingMore) {
                        item { CircularProgressIndicator(modifier = Modifier.padding(8.dp)) }
                    }
                    if (state.books.isNotEmpty()) {
                        item {
                            Text(
                                text = "Cached results: ${state.books.size}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 6.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
