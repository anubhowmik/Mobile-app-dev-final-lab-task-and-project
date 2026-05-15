package com.example.mobileappdevfinalproject.utils

import android.util.Patterns

/**
 * ValidationUtils holds reusable validation functions.
 * Instead of writing the same email check in every activity,
 * we write it once here and call it everywhere.
 */
object ValidationUtils {

    /**
     * Check if an email address is in valid format (e.g., user@example.com).
     * Uses Android's built-in Patterns class.
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Check if a password meets the minimum requirements.
     * At least 6 characters (Firebase minimum).
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    /**
     * Check if two passwords match (for confirm password field).
     */
    fun passwordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }
}
