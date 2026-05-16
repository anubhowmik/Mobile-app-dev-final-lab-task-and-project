package com.example.mobileappdevfinalproject.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileappdevfinalproject.databinding.ActivityEmailVerificationBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class EmailVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmailVerificationBinding
    private val auth = Firebase.auth

    // This handler will check every 3 seconds if email is verified
    private val handler = Handler(Looper.getMainLooper())
    private val checkVerificationRunnable = object : Runnable {
        override fun run() {
            checkEmailVerification()
            handler.postDelayed(this, 3000) // Check again after 3 seconds
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Show user's email on screen
        binding.tvEmailAddress.text = auth.currentUser?.email ?: "your email"

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Resend verification email
        binding.btnResendEmail.setOnClickListener {
            auth.currentUser?.sendEmailVerification()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Verification email resent!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to resend email", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Manual check button
        binding.btnCheckVerification.setOnClickListener {
            checkEmailVerification()
        }

        // Go back to Login
        binding.tvBackToLogin.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun checkEmailVerification() {
        // Reload the user from Firebase to get the latest info
        auth.currentUser?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (auth.currentUser?.isEmailVerified == true) {
                    Toast.makeText(this, "Email verified! Welcome!", Toast.LENGTH_SHORT).show()
                    handler.removeCallbacks(checkVerificationRunnable) // Stop checking
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Email not verified yet. Please check your inbox.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Start auto-checking when screen is visible
        handler.post(checkVerificationRunnable)
    }

    override fun onPause() {
        super.onPause()
        // Stop auto-checking when screen is not visible (saves battery)
        handler.removeCallbacks(checkVerificationRunnable)
    }
}
