package com.example.booksapp.domain.repository

import com.example.booksapp.domain.model.AppUser
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<AppUser?>

    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String, displayName: String): Result<Unit>
    fun signOut()
}
