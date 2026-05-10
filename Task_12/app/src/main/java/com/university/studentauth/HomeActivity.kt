package com.university.studentauth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.university.studentauth.databinding.ActivityHomeBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        loadUserInfo()

        binding.btnLogout.setOnClickListener { logoutUser() }
        binding.btnUpdatePassword.setOnClickListener { updatePassword() }
        binding.btnDeleteAccount.setOnClickListener { confirmDeleteAccount() }
    }

    private fun loadUserInfo() {
        val user = auth.currentUser ?: return
        val email = user.email ?: "N/A"
        val uid = user.uid.take(8) + "..."
        val date = user.metadata?.creationTimestamp?.let {
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
        } ?: "N/A"

        binding.tvEmail.text = email
        binding.tvUid.text = "UID: $uid"
        binding.tvCreated.text = "Member since: $date"
    }

    private fun logoutUser() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun updatePassword() {
        val newPass = binding.etNewPassword.text.toString().trim()
        val confirmPass = binding.etConfirmNewPassword.text.toString().trim()

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            Snackbar.make(binding.root, "Please fill in both fields", Snackbar.LENGTH_SHORT).show()
            return
        }
        if (newPass.length < 8) {
            Snackbar.make(binding.root, "Password must be at least 8 characters",
                Snackbar.LENGTH_SHORT).show()
            return
        }
        if (newPass != confirmPass) {
            Snackbar.make(binding.root, "Passwords do not match", Snackbar.LENGTH_SHORT).show()
            return
        }

        auth.currentUser?.updatePassword(newPass)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                binding.etNewPassword.text?.clear()
                binding.etConfirmNewPassword.text?.clear()
            } else {
                Snackbar.make(binding.root,
                    task.exception?.message ?: "Update failed",
                    Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun confirmDeleteAccount() {
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to permanently delete your account? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ -> deleteAccount() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteAccount() {
        auth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Snackbar.make(binding.root,
                    task.exception?.message ?: "Deletion failed",
                    Snackbar.LENGTH_LONG).show()
            }
        }
    }
}