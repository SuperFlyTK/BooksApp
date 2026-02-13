package com.example.booksapp.ui.favorites

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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val books: List<Book> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val state: LoadState = LoadState.Loading,
    val infoMessage: String? = null,
)

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val favoritesRepository: FavoritesRepository,
    private val booksRepository: BooksRepository,
) : ViewModel() {

    private val mutableState = MutableStateFlow(FavoritesUiState())
    val state: StateFlow<FavoritesUiState> = mutableState.asStateFlow()

    init {
        authRepository.currentUser
            .flatMapLatest { user ->
                if (user == null) {
                    flowOf(emptySet())
                } else {
                    favoritesRepository.observeFavoriteIds(user.uid)
                }
            }
            .onEach { ids ->
                mutableState.update {
                    it.copy(
                        favoriteIds = ids,
                        state = if (ids.isEmpty()) LoadState.Empty else LoadState.Loading,
                        infoMessage = null,
                    )
                }
            }
            .flatMapLatest { ids ->
                if (ids.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    booksRepository.observeBooksByIds(ids)
                }
            }
            .onEach { books ->
                mutableState.update { current ->
                    current.copy(
                        books = books,
                        state = when {
                            current.favoriteIds.isEmpty() -> LoadState.Empty
                            books.isEmpty() -> LoadState.Empty
                            else -> LoadState.Data
                        },
                        infoMessage = if (
                            current.favoriteIds.isNotEmpty() &&
                            books.size < current.favoriteIds.size
                        ) {
                            "Some favorites are not available in local cache yet"
                        } else {
                            null
                        },
                    )
                }
            }
            .launchIn(viewModelScope)
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
