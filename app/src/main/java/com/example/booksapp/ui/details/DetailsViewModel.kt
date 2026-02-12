package com.example.booksapp.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksapp.domain.model.Book
import com.example.booksapp.domain.model.BookComment
import com.example.booksapp.domain.repository.AuthRepository
import com.example.booksapp.domain.repository.BooksRepository
import com.example.booksapp.domain.repository.CommentsRepository
import com.example.booksapp.domain.repository.FavoritesRepository
import com.example.booksapp.domain.usecase.RecommendationScorer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailsUiState(
    val book: Book? = null,
    val comments: List<BookComment> = emptyList(),
    val isFavorite: Boolean = false,
    val recommendationScore: Double = 0.0,
    val infoMessage: String? = null,
)

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val booksRepository: BooksRepository,
    private val favoritesRepository: FavoritesRepository,
    private val commentsRepository: CommentsRepository,
    private val authRepository: AuthRepository,
    private val recommendationScorer: RecommendationScorer,
) : ViewModel() {

    private val bookId: String = checkNotNull(savedStateHandle["bookId"])

    private val mutableState = MutableStateFlow(DetailsUiState())
    val state: StateFlow<DetailsUiState> = mutableState

    init {
        booksRepository.observeBookById(bookId)
            .combine(
                authRepository.currentUser.flatMapLatest { user ->
                    if (user == null) {
                        flowOf(emptySet())
                    } else {
                        favoritesRepository.observeFavoriteIds(user.uid)
                    }
                },
            ) { book, favorites ->
                val score = if (book == null) {
                    0.0
                } else {
                    recommendationScorer.score(
                        book = book,
                        preferredSubjects = setOf("fantasy", "fiction", "science"),
                        isFavorite = favorites.contains(book.id),
                    )
                }
                mutableState.update {
                    it.copy(
                        book = book,
                        isFavorite = book?.id?.let(favorites::contains) == true,
                        recommendationScore = score,
                    )
                }
            }
            .launchIn(viewModelScope)

        commentsRepository.observeComments(bookId)
            .onEach { comments -> mutableState.update { it.copy(comments = comments) } }
            .launchIn(viewModelScope)
    }

    fun toggleFavorite() {
        val book = state.value.book ?: return
        val user = authRepository.currentUser.value ?: run {
            mutableState.update { it.copy(infoMessage = "Sign in to add favorites") }
            return
        }
        viewModelScope.launch {
            val result = favoritesRepository.toggleFavorite(user.uid, book)
            if (result.isFailure) {
                mutableState.update { it.copy(infoMessage = result.exceptionOrNull()?.localizedMessage) }
            }
        }
    }

    fun deleteComment(commentId: String) {
        val user = authRepository.currentUser.value ?: return
        viewModelScope.launch {
            val result = commentsRepository.deleteComment(bookId, commentId, user.uid)
            if (result.isFailure) {
                mutableState.update { it.copy(infoMessage = result.exceptionOrNull()?.localizedMessage) }
            }
        }
    }
}
