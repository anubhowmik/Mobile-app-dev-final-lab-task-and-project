package com.example.mobileappdevfinalproject.repository


import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Repository handles all Firebase operations.
 * Using 'suspend' functions means these run on a background thread (coroutine).
 * The ViewModel calls these; Activities never talk to Firebase directly.
 */
class AuthRepository {

    private val auth = Firebase.auth

    /**
     * Login existing user.
     * Returns Result.success(user) on success, Result.failure(exception) on error.
     */
    suspend fun loginUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User not found")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Register new user and send verification email automatically.
     */
    suspend fun registerUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User creation failed")
            // Automatically send verification email after registration
            user.sendEmailVerification().await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Send password reset email.
     */
    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
