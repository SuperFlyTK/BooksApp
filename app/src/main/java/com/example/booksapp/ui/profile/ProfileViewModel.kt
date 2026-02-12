package com.example.booksapp.ui.profile

import androidx.lifecycle.ViewModel
import com.example.booksapp.domain.model.AppUser
import com.example.booksapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    val user: StateFlow<AppUser?> = authRepository.currentUser

    fun signOut() {
        authRepository.signOut()
    }
}
