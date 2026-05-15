package com.example.mobileappdevfinalproject.utils


import android.content.Context
import android.content.SharedPreferences

/**
 * PreferenceManager handles saving small pieces of data locally on the device.
 * We use it for the "Remember Me" checkbox feature.
 * SharedPreferences stores key-value pairs that survive app restarts.
 */
class PreferenceManager(context: Context) {

    companion object {
        private const val PREF_NAME = "LoginPreferences"   // Name of the file saved on device
        private const val KEY_REMEMBER_ME = "remember_me"  // Key to store the boolean value
    }

    // Create or open a SharedPreferences file
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * Save the Remember Me state.
     * Call this when user logs in and ticks the checkbox.
     */
    fun setRememberMe(rememberMe: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_REMEMBER_ME, rememberMe)
            .apply() // .apply() saves in background (non-blocking)
    }

    /**
     * Read the Remember Me state.
     * Returns false by default if nothing was saved yet.
     */
    fun isRememberMe(): Boolean {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)
    }

    /**
     * Clear all saved preferences (used on logout).
     */
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}
