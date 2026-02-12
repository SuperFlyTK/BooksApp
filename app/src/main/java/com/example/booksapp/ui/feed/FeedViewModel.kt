package com.example.booksapp.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksapp.domain.model.Book
import com.example.booksapp.domain.repository.AuthRepository
import com.example.booksapp.domain.repository.BooksRepository
import com.example.booksapp.domain.repository.FavoritesRepository
import com.example.booksapp.ui.common.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedUiState(
    val books: List<Book> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val state: LoadState = LoadState.Loading,
    val isLoadingMore: Boolean = false,
    val infoMessage: String? = null,
)

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModel @Inject constructor(
    private val booksRepository: BooksRepository,
    private val favoritesRepository: FavoritesRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val mutableState = MutableStateFlow(FeedUiState())
    val state: StateFlow<FeedUiState> = mutableState.asStateFlow()

    init {
        booksRepository.observeFeedBooks()
            .onEach { books ->
                mutableState.update {
                    it.copy(
                        books = books,
                        state = when {
                            books.isEmpty() -> LoadState.Empty
                            else -> LoadState.Data
                        },
                    )
                }
            }
            .launchIn(viewModelScope)

        observeFavorites()
        refresh(force = false)
    }

    private fun observeFavorites() {
        authRepository.currentUser
            .flatMapLatest { user ->
                if (user == null) {
                    MutableStateFlow(emptySet())
                } else {
                    favoritesRepository.observeFavoriteIds(user.uid)
                }
            }
            .onEach { ids -> mutableState.update { it.copy(favoriteIds = ids) } }
            .launchIn(viewModelScope)
    }

    fun refresh(force: Boolean = true) {
        viewModelScope.launch {
            mutableState.update { it.copy(state = LoadState.Loading, infoMessage = null) }
            val result = booksRepository.refreshFeed(force)
            if (result.isFailure) {
                mutableState.update {
                    it.copy(
                        state = if (it.books.isEmpty()) {
                        LoadState.Error(result.exceptionOrNull()?.localizedMessage ?: "Loading error")
                        } else {
                            LoadState.Data
                        },
                        infoMessage = "Showing cache. ${result.exceptionOrNull()?.localizedMessage.orEmpty()}",
                    )
                }
            }
        }
    }

    fun loadMore() {
        if (mutableState.value.isLoadingMore) return
        viewModelScope.launch {
            mutableState.update { it.copy(isLoadingMore = true) }
            val result = booksRepository.loadMoreFeed()
            mutableState.update {
                it.copy(
                    isLoadingMore = false,
                    infoMessage = result.exceptionOrNull()?.localizedMessage,
                )
            }
        }
    }

    fun toggleFavorite(book: Book) {
        val user = authRepository.currentUser.value ?: run {
            mutableState.update { it.copy(infoMessage = "Please sign in to use favorites") }
            return
        }
        viewModelScope.launch {
            val result = favoritesRepository.toggleFavorite(user.uid, book)
            if (result.isFailure) {
                mutableState.update { it.copy(infoMessage = result.exceptionOrNull()?.localizedMessage) }
            }
        }
    }
}
