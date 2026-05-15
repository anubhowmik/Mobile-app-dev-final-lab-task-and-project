package com.example.mobileappdevfinalproject.activities


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileappdevfinalproject.databinding.ActivitySplashBinding
import com.example.mobileappdevfinalproject.utils.PreferenceManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(this)

        // Wait 2 seconds on splash screen, then decide where to go
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 2000)
    }

    private fun navigateToNextScreen() {
        val currentUser = Firebase.auth.currentUser
        val rememberMe = preferenceManager.isRememberMe()

        when {
            // User is logged in AND chose Remember Me → go straight to Home
            currentUser != null && rememberMe -> {
                startActivity(Intent(this, HomeActivity::class.java))
            }
            // Otherwise → go to Login
            else -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
        finish() // Close splash so user can't press Back to return
    }
}
