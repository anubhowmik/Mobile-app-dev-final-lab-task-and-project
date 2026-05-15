package com.example.mobileappdevfinalproject.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mobileappdevfinalproject.databinding.ActivityLoginBinding
import com.example.mobileappdevfinalproject.utils.PreferenceManager
import com.example.mobileappdevfinalproject.utils.ValidationUtils
import com.example.mobileappdevfinalproject.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        preferenceManager = PreferenceManager(this)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        // Login button
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                authViewModel.loginUser(email, password)
            }
        }

        // Go to SignUp screen
        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Forgot password
        binding.tvForgotPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email first", Toast.LENGTH_SHORT).show()
            } else {
                authViewModel.sendPasswordReset(email)
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        if (!ValidationUtils.isValidEmail(email)) {
            binding.etEmail.error = "Enter a valid email address"
            isValid = false
        } else {
            binding.etEmail.error = null
        }

        if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.etPassword.error = null
        }

        return isValid
    }

    private fun observeViewModel() {
        // Watch for login result
        authViewModel.loginResult.observe(this) { result ->
            result.onSuccess { user ->
                if (user.isEmailVerified) {
                    // Save Remember Me preference
                    preferenceManager.setRememberMe(binding.cbRememberMe.isChecked)
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Please verify your email first", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, EmailVerificationActivity::class.java))
                }
            }
            result.onFailure { exception ->
                Toast.makeText(this, exception.message ?: "Login failed", Toast.LENGTH_SHORT).show()
            }
        }

        // Watch loading state
        authViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !isLoading
        }

        // Watch password reset result
        authViewModel.passwordResetResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_SHORT).show()
            }
            result.onFailure { exception ->
                Toast.makeText(this, exception.message ?: "Failed to send reset email", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
