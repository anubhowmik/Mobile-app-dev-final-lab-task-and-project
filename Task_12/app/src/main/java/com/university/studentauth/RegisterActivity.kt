package com.university.studentauth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.university.studentauth.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener { registerUser() }
        binding.tvLoginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun registerUser() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirm = binding.etConfirmPassword.text.toString().trim()

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showSnackbar("All fields are required")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showSnackbar("Please enter a valid email address")
            return
        }
        if (password.length < 8) {
            showSnackbar("Password must be at least 8 characters")
            return
        }
        if (password != confirm) {
            showSnackbar("Passwords do not match")
            return
        }

        showProgress(true)

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            task -> showProgress(false)

            if(task.isSuccessful) {
                showSnackbar("Registration successful")

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                showSnackbar(task.exception?.message ?: "Registration failed")
            }
        }
    }

    private fun showProgress(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !show
    }

    private fun showSnackbar(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
    }
}