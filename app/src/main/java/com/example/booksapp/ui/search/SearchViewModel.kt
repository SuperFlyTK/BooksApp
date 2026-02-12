package com.example.booksapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksapp.domain.model.Book
import com.example.booksapp.domain.repository.BooksRepository
import com.example.booksapp.domain.usecase.QuerySanitizer
import com.example.booksapp.ui.common.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val books: List<Book> = emptyList(),
    val state: LoadState = LoadState.Idle,
    val isLoadingMore: Boolean = false,
    val infoMessage: String? = null,
)

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel @Inject constructor(
    private val booksRepository: BooksRepository,
    private val querySanitizer: QuerySanitizer,
) : ViewModel() {

    private val mutableState = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = mutableState.asStateFlow()

    private val queryState = MutableStateFlow("")

    init {
        observeSearchResults()
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchResults() {
        queryState
            .debounce(450)
            .distinctUntilChanged()
            .onEach { raw ->
                val sanitized = querySanitizer.sanitize(raw)
                if (sanitized.isBlank()) {
                    mutableState.update { it.copy(books = emptyList(), state = LoadState.Idle) }
                } else {
                    refreshCurrentQuery(sanitized)
                }
            }
            .flatMapLatest { raw ->
                val sanitized = querySanitizer.sanitize(raw)
                if (sanitized.isBlank()) {
                    flowOf(emptyList())
                } else {
                    booksRepository.observeSearchBooks(sanitized).stateIn(
                        viewModelScope,
                        SharingStarted.WhileSubscribed(5_000),
                        emptyList(),
                    )
                }
            }
            .onEach { books ->
                mutableState.update {
                    it.copy(
                        books = books,
                        state = when {
                            it.query.isBlank() -> LoadState.Idle
                            books.isEmpty() -> LoadState.Empty
                            else -> LoadState.Data
                        },
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(value: String) {
        mutableState.update { it.copy(query = value) }
        queryState.value = value
    }

    fun retryCurrentQuery() {
        val query = querySanitizer.sanitize(mutableState.value.query)
        if (query.isBlank()) return
        viewModelScope.launch {
            refreshCurrentQuery(query)
        }
    }

    fun loadMore() {
        val query = querySanitizer.sanitize(mutableState.value.query)
        if (query.isBlank() || mutableState.value.isLoadingMore) return
        viewModelScope.launch {
            mutableState.update { it.copy(isLoadingMore = true) }
            val result = booksRepository.loadMoreSearch(query)
            mutableState.update {
                it.copy(
                    isLoadingMore = false,
                    infoMessage = result.exceptionOrNull()?.localizedMessage,
                )
            }
        }
    }

    private suspend fun refreshCurrentQuery(sanitizedQuery: String) {
        mutableState.update { it.copy(state = LoadState.Loading, infoMessage = null) }
        val result = booksRepository.refreshSearch(sanitizedQuery)
        if (result.isFailure) {
            mutableState.update {
                it.copy(
                    state = if (it.books.isEmpty()) {
                        LoadState.Error(result.exceptionOrNull()?.localizedMessage ?: "Search failed")
                    } else {
                        LoadState.Data
                    },
                    infoMessage = "Showing cache. ${result.exceptionOrNull()?.localizedMessage.orEmpty()}",
                )
            }
        }
    }
}
