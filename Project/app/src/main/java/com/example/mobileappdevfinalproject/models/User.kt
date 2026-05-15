package com.example.mobileappdevfinalproject.models

/**
 * User is a data class — a simple container for user information.
 * Data classes automatically get equals(), hashCode(), toString(), and copy() methods.
 * This model represents a user in our app.
 */
data class User(
    val uid: String = "",           // Firebase unique ID for this user
    val email: String = "",         // User's email address
    val isEmailVerified: Boolean = false  // Whether they verified their email
)
