package com.example.mobileappdevfinalproject.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mobileappdevfinalproject.databinding.ActivityForgotPasswordBinding
import com.example.mobileappdevfinalproject.viewmodel.AuthViewModel

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.btnResetPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                binding.etEmail.error = "Email is required"
                return@setOnClickListener
            }
            viewModel.sendPasswordReset(email)
        }

        viewModel.passwordResetResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG).show()
                finish()
            }
            result.onFailure { exception ->
                Toast.makeText(this, exception.message ?: "Failed to send reset link", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
