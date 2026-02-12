package com.example.booksapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksapp.domain.model.AppUser
import com.example.booksapp.domain.repository.AuthRepository
import com.example.booksapp.domain.usecase.CredentialsValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val isSignUpMode: Boolean = false,
    val isSubmitting: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val nameError: String? = null,
    val generalError: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val credentialsValidator: CredentialsValidator,
) : ViewModel() {

    val currentUser: StateFlow<AppUser?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), authRepository.currentUser.value)

    private val mutableState = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = mutableState.asStateFlow()

    fun onEmailChanged(value: String) {
        mutableState.update { it.copy(email = value, emailError = null, generalError = null) }
    }

    fun onPasswordChanged(value: String) {
        mutableState.update { it.copy(password = value, passwordError = null, generalError = null) }
    }

    fun onDisplayNameChanged(value: String) {
        mutableState.update { it.copy(displayName = value, nameError = null, generalError = null) }
    }

    fun toggleMode() {
        mutableState.update {
            it.copy(
                isSignUpMode = !it.isSignUpMode,
                generalError = null,
                emailError = null,
                passwordError = null,
                nameError = null,
            )
        }
    }

    fun submit() {
        val snapshot = state.value
        val validation = if (snapshot.isSignUpMode) {
            credentialsValidator.validateSignUp(snapshot.email, snapshot.password, snapshot.displayName)
        } else {
            credentialsValidator.validateLogin(snapshot.email, snapshot.password)
        }

        if (!validation.isValid) {
            mutableState.update {
                it.copy(
                    emailError = validation.emailError,
                    passwordError = validation.passwordError,
                    nameError = validation.nameError,
                )
            }
            return
        }

        viewModelScope.launch {
            mutableState.update { it.copy(isSubmitting = true, generalError = null) }
            val result = if (snapshot.isSignUpMode) {
                authRepository.signUp(
                    email = snapshot.email.trim(),
                    password = snapshot.password,
                    displayName = snapshot.displayName.trim(),
                )
            } else {
                authRepository.signIn(snapshot.email.trim(), snapshot.password)
            }
            mutableState.update {
                it.copy(
                    isSubmitting = false,
                    generalError = result.exceptionOrNull()?.localizedMessage,
                )
            }
        }
    }
}
