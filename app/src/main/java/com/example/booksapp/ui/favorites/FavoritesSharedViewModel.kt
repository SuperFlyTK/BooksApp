package com.example.booksapp.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksapp.domain.model.Book
import com.example.booksapp.domain.repository.AuthRepository
import com.example.booksapp.domain.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesSharedViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {
    private val mutableFavoriteIds = MutableStateFlow(emptySet<String>())
    val favoriteIds: StateFlow<Set<String>> = mutableFavoriteIds

    init {
        authRepository.currentUser
            .flatMapLatest { user ->
                if (user == null) MutableStateFlow(emptySet())
                else favoritesRepository.observeFavoriteIds(user.uid)
            }
            .onEach { ids -> mutableFavoriteIds.value = ids }
            .launchIn(viewModelScope)
    }

    fun toggleFavorite(book: Book) {
        val user = authRepository.currentUser.value ?: return
        viewModelScope.launch {
            favoritesRepository.toggleFavorite(user.uid, book)
        }
    }
}
