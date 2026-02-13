package com.example.booksapp.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
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
fun FavoritesScreen(
    snackbarHostState: SnackbarHostState,
    onBookClick: (Book) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(state.infoMessage) {
        val message = state.infoMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
    }

    when (val loadState = state.state) {
        LoadState.Loading -> LoadingContent()
        LoadState.Empty -> {
            if (state.favoriteIds.isEmpty()) {
                EmptyContent(message = "No favorites yet")
            } else {
                EmptyContent(message = "No favorite books found in local cache yet")
            }
        }
        is LoadState.Error -> ErrorContent(message = loadState.message, onRetry = {})
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    Text(
                        text = "Favorites: ${state.favoriteIds.size}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
                items(state.books, key = { it.id }) { book ->
                    BookCard(
                        book = book,
                        isFavorite = true,
                        onClick = { onBookClick(book) },
                        onFavoriteClick = { viewModel.toggleFavorite(book) },
                    )
                }
            }
        }
    }
}
