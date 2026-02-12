package com.example.booksapp.ui.common

sealed interface LoadState {
    data object Idle : LoadState
    data object Loading : LoadState
    data object Empty : LoadState
    data class Error(val message: String) : LoadState
    data object Data : LoadState
}
