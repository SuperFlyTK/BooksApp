package com.example.booksapp.domain.usecase

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CredentialsValidatorTest {
    private val validator = CredentialsValidator()

    @Test
    fun `valid login passes`() {
        val result = validator.validateLogin("user@example.com", "password123")
        assertThat(result.isValid).isTrue()
        assertThat(result.emailError).isNull()
        assertThat(result.passwordError).isNull()
    }

    @Test
    fun `invalid login reports errors`() {
        val result = validator.validateLogin("invalid", "short")
        assertThat(result.isValid).isFalse()
        assertThat(result.emailError).isNotNull()
        assertThat(result.passwordError).isNotNull()
    }

    @Test
    fun `sign up requires name`() {
        val result = validator.validateSignUp("test@example.com", "password123", "A")
        assertThat(result.isValid).isFalse()
        assertThat(result.nameError).isNotNull()
    }

    @Test
    fun `email is validated after trimming spaces`() {
        val result = validator.validateLogin("  user@example.com  ", "password123")
        assertThat(result.isValid).isTrue()
    }
}
