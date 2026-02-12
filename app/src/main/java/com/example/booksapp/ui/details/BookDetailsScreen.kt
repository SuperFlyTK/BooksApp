package com.example.booksapp.ui.details
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.booksapp.domain.model.BookComment
import com.example.booksapp.ui.components.CommentItem

@Composable
fun BookDetailsScreen(
    currentUserId: String?,
    snackbarHostState: SnackbarHostState,
    onOpenEditor: (comment: BookComment?) -> Unit,
    viewModel: DetailsViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val book = state.book

    LaunchedEffect(state.infoMessage) {
        val message = state.infoMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
    }

        if (book == null) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                Text(text = "Book not found", modifier = Modifier.padding(16.dp))
            }
            return
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            )
            Text(text = book.title, style = MaterialTheme.typography.headlineSmall)
            Text(text = book.author, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Recommendation score: ${"%.2f".format(state.recommendationScore)}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Button(
                onClick = viewModel::toggleFavorite,
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text(if (state.isFavorite) "Remove favorite" else "Add to favorites")
            }
            Button(
                onClick = { onOpenEditor(null) },
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text("Write a review")
            }
            Text(
                text = "Comments",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
        items(state.comments, key = { it.id }) { comment ->
            CommentItem(
                comment = comment,
                canManage = currentUserId == comment.userId,
                onEdit = { onOpenEditor(comment) },
                onDelete = { viewModel.deleteComment(comment.id) },
            )
        }
    }
}
