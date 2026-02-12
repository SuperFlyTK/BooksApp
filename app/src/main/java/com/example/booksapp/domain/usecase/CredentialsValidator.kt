package com.example.booksapp.domain.usecase

data class ValidationResult(
    val isValid: Boolean,
    val emailError: String? = null,
    val passwordError: String? = null,
    val nameError: String? = null,
)

class CredentialsValidator {
    fun validateLogin(email: String, password: String): ValidationResult {
        val emailError = if (!EMAIL_REGEX.matches(email.trim())) "Enter a valid email" else null
        val passwordError = if (password.length < 8) "Password must be at least 8 characters" else null
        return ValidationResult(
            isValid = emailError == null && passwordError == null,
            emailError = emailError,
            passwordError = passwordError,
        )
    }

    fun validateSignUp(email: String, password: String, displayName: String): ValidationResult {
        val loginValidation = validateLogin(email, password)
        val nameError = if (displayName.trim().length < 2) "Name must be at least 2 characters" else null
        return loginValidation.copy(
            isValid = loginValidation.isValid && nameError == null,
            nameError = nameError,
        )
    }

    private companion object {
        val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    }
}
