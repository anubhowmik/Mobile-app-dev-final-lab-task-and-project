package com.example.mobileappdevfinalproject.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileappdevfinalproject.Adapter.BrandsAdapter
import com.example.mobileappdevfinalproject.databinding.ActivityHomeBinding
import com.example.mobileappdevfinalproject.utils.PreferenceManager
import com.example.mobileappdevfinalproject.viewmodel.HomeViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class HomeActivity : AppCompatActivity() {
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    private lateinit var binding: ActivityHomeBinding
    private val brandsAdapter = BrandsAdapter(mutableListOf())
    private lateinit var preferenceManager: PreferenceManager
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()

        preferenceManager = PreferenceManager(this)

        // Show the logged-in user's email
        val userEmail = auth.currentUser?.email ?: "User"
        binding.tvWelcome.text = "Welcome!\n$userEmail"

        setupClickListeners()
    }

    private fun initUI() {
       initBrands()
    }

    private fun initBrands() {
        binding.recyclerViewBrands.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewBrands.adapter = brandsAdapter
        binding.progressBarCategory.visibility = View.VISIBLE

        viewModel.brands.observe(this) { data ->
            brandsAdapter.updateData(data)
            binding.progressBarCategory.visibility = View.GONE
        }
        viewModel.loadBrands()
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
