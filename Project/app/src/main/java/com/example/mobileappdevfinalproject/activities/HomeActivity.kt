package com.example.mobileappdevfinalproject.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileappdevfinalproject.databinding.ActivityHomeBinding
import com.example.mobileappdevfinalproject.utils.PreferenceManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var preferenceManager: PreferenceManager
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(this)

        // Show the logged-in user's email
        val userEmail = auth.currentUser?.email ?: "User"
        binding.tvWelcome.text = "Welcome!\n$userEmail"

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        // Clear Remember Me preference
        preferenceManager.setRememberMe(false)
        // Sign out from Firebase
        auth.signOut()
        // Go back to Login screen
        startActivity(Intent(this, LoginActivity::class.java))
        // Clear all activities from the back stack
        finishAffinity()
    }

    // Prevent going back to Login when already logged in
    override fun onBackPressed() {
        super.onBackPressed()
        showLogoutConfirmation()
    }
}
