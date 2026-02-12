package com.example.booksapp.ui.review

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksapp.domain.repository.AuthRepository
import com.example.booksapp.domain.repository.CommentsRepository
import com.example.booksapp.domain.usecase.ReviewValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReviewEditorUiState(
    val text: String = "",
    val rating: Int = 5,
    val textError: String? = null,
    val ratingError: String? = null,
    val isSubmitting: Boolean = false,
    val infoMessage: String? = null,
    val saved: Boolean = false,
)

@HiltViewModel
class ReviewEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val commentsRepository: CommentsRepository,
    private val reviewValidator: ReviewValidator,
) : ViewModel() {

    val bookId: String = checkNotNull(savedStateHandle["bookId"])
    private val commentId: String = savedStateHandle["commentId"] ?: ""

    private val mutableState = MutableStateFlow(
        ReviewEditorUiState(
            text = Uri.decode(savedStateHandle["initialText"] ?: ""),
            rating = (savedStateHandle["initialRating"] ?: "5").toIntOrNull()?.coerceIn(1, 5) ?: 5,
        ),
    )
    val state: StateFlow<ReviewEditorUiState> = mutableState.asStateFlow()

    fun onTextChanged(value: String) {
        mutableState.update { it.copy(text = value, textError = null, infoMessage = null) }
    }

    fun onRatingChanged(value: Int) {
        mutableState.update { it.copy(rating = value.coerceIn(1, 5), ratingError = null, infoMessage = null) }
    }

    fun submit() {
        val snapshot = state.value
        val validation = reviewValidator.validate(snapshot.text, snapshot.rating)
        if (!validation.isValid) {
            mutableState.update { it.copy(textError = validation.textError, ratingError = validation.ratingError) }
            return
        }
        val user = authRepository.currentUser.value ?: run {
            mutableState.update { it.copy(infoMessage = "You must be signed in to post") }
            return
        }

        viewModelScope.launch {
            mutableState.update { it.copy(isSubmitting = true, infoMessage = null) }
            val result = if (commentId.isBlank()) {
                commentsRepository.addComment(
                    bookId = bookId,
                    userId = user.uid,
                    userName = user.displayName,
                    text = snapshot.text,
                    rating = snapshot.rating,
                )
            } else {
                commentsRepository.updateComment(
                    bookId = bookId,
                    commentId = commentId,
                    userId = user.uid,
                    text = snapshot.text,
                    rating = snapshot.rating,
                )
            }
            mutableState.update {
                it.copy(
                    isSubmitting = false,
                    saved = result.isSuccess,
                    infoMessage = result.exceptionOrNull()?.localizedMessage,
                )
            }
        }
    }
}
