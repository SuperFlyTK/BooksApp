package com.example.booksapp.data.repository

import com.example.booksapp.domain.model.AppUser
import com.example.booksapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth?,
) : AuthRepository {

    private val mutableUser = MutableStateFlow(firebaseAuth?.currentUser?.toAppUser())
    override val currentUser: StateFlow<AppUser?> = mutableUser.asStateFlow()

    private val listener = FirebaseAuth.AuthStateListener { auth ->
        mutableUser.value = auth.currentUser?.toAppUser()
    }

    init {
        firebaseAuth?.addAuthStateListener(listener)
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        val auth = firebaseAuth ?: return Result.failure(IllegalStateException("Firebase not configured"))
        return runCatching {
            auth.signInWithEmailAndPassword(email, password).await()
            Unit
        }
    }

    override suspend fun signUp(email: String, password: String, displayName: String): Result<Unit> {
        val auth = firebaseAuth ?: return Result.failure(IllegalStateException("Firebase not configured"))
        return runCatching {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val profileRequest = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName.trim())
                .build()
            authResult.user?.updateProfile(profileRequest)?.await()
            Unit
        }
    }

    override fun signOut() {
        firebaseAuth?.signOut()
        mutableUser.value = null
    }
}

private fun com.google.firebase.auth.FirebaseUser.toAppUser(): AppUser {
    return AppUser(
        uid = uid,
        email = email.orEmpty(),
        displayName = displayName.orEmpty().ifBlank { email.orEmpty().substringBefore("@") },
    )
}
