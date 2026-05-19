package com.example.mobileappdevfinalproject.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mobileappdevfinalproject.databinding.ActivityLoginBinding
import com.example.mobileappdevfinalproject.utils.PreferenceManager
import com.example.mobileappdevfinalproject.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {


    companion object {
        const val ADMIN_EMAIL = "anupombhowmik81@gmail.com"
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        preferenceManager = PreferenceManager(this)

        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email    = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.etEmail.error = "Email is required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.etPassword.error = "Password is required"
                return@setOnClickListener
            }
            viewModel.loginUser(email, password)
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled     = !isLoading
        }

        viewModel.loginResult.observe(this) { result ->
            result.onSuccess { user ->
                val loggedInEmail = user.email ?: ""

                if (loggedInEmail.equals(ADMIN_EMAIL, ignoreCase = true)) {
                    // ── Admin login → go straight to Admin Panel ──────────
                    Toast.makeText(this, "Welcome, Admin!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AdminActivity::class.java))
                } else {
                    // ── Regular user → check email verification ───────────
                    if (!user.isEmailVerified) {
                        startActivity(Intent(this, EmailVerificationActivity::class.java))
                    } else {
                        if (binding.cbRememberMe.isChecked) {
                            preferenceManager.setRememberMe(true)
                        }
                        startActivity(Intent(this, HomeActivity::class.java))
                    }
                }
                finish()
            }
            result.onFailure { exception ->
                Toast.makeText(this, exception.message ?: "Login failed", Toast.LENGTH_LONG).show()
            }
        }
    }
}