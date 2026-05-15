package com.example.mobileappdevfinalproject.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mobileappdevfinalproject.databinding.ActivitySignupBinding
import com.example.mobileappdevfinalproject.utils.ValidationUtils
import com.example.mobileappdevfinalproject.viewmodel.AuthViewModel

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnSignup.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (validateInputs(email, password, confirmPassword)) {
                authViewModel.registerUser(email, password)
            }
        }

        // Go back to Login
        binding.tvLogin.setOnClickListener {
            finish() // Closes SignUpActivity, goes back to LoginActivity
        }
    }

    private fun validateInputs(email: String, password: String, confirmPassword: String): Boolean {
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

        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Passwords do not match"
            isValid = false
        } else {
            binding.etConfirmPassword.error = null
        }

        return isValid
    }

    private fun observeViewModel() {
        authViewModel.registerResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Account created! Please check your email to verify.", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, EmailVerificationActivity::class.java))
                finish()
            }
            result.onFailure { exception ->
                Toast.makeText(this, exception.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSignup.isEnabled = !isLoading
        }
    }
}
