package com.university.usersettings

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ProfileActivity: AppCompatActivity() {

    private lateinit var tvWelcomeBanner : TextView
    private lateinit var etStudentId : EditText
    private lateinit var etFullName : EditText
    private lateinit var spinnerDept : Spinner
    private lateinit var spinnerYear : Spinner
    private lateinit var etEmail : EditText
    private lateinit var btnSaveProfile : MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Profile Setup"

        tvWelcomeBanner = findViewById(R.id.tvWelcomeBanner)
        etStudentId = findViewById(R.id.etStudentId)
        etFullName = findViewById(R.id.etFullName)
        spinnerDept = findViewById(R.id.spinnerDept)
        spinnerYear = findViewById(R.id.spinnerYear)
        etEmail = findViewById(R.id.etEmail)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)

        val deptAdapter = ArrayAdapter.createFromResource(
            this, R.array.departments, android.R.layout.simple_spinner_item
        )
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDept.adapter = deptAdapter

        val yearAdapter = ArrayAdapter.createFromResource(
            this, R.array.years, android.R.layout.simple_spinner_item
        )
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYear.adapter = yearAdapter

        loadSavedProfile()

        btnSaveProfile.setOnClickListener { saveProfile() }
    }

    override fun onResume() {
        super.onResume()
        loadSavedProfile()
    }

    private fun loadSavedProfile(){
        val prefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
        val savedName = prefs.getString("KEY_STUDENT_NAME", "") ?: ""

        tvWelcomeBanner.text = if (savedName.isNotEmpty()) {
            "Welcome back, $savedName!"
        } else {
            "Welcome! Set up your profile."
        }

        etStudentId.setText(prefs.getString("KEY_STUDENT_ID", ""))
        etFullName.setText(prefs.getString("KEY_STUDENT_NAME", ""))
        etEmail.setText(prefs.getString("KEY_EMAIL", ""))

        val departments = resources.getStringArray(R.array.departments)
        val savedDept = prefs.getString("KEY_DEPARTMENT", "") ?: ""
        val deptIndex = departments.indexOf(savedDept).coerceAtLeast(0)
        spinnerDept.setSelection(deptIndex)

        val years = resources.getStringArray(R.array.years)
        val savedYear = prefs.getString("KEY_YEAR", "") ?: ""
        val yearIndex = years.indexOf(savedYear).coerceAtLeast(0)
        spinnerYear.setSelection(yearIndex)
    }

    private fun saveProfile(){
        val name = etFullName.text.toString().trim()
        val id = etStudentId.text.toString().trim()
        val dept = spinnerDept.selectedItem.toString()
        val year = spinnerYear.selectedItem.toString()
        val email = etEmail.text.toString().trim()

        if (name.isEmpty() || id.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString("KEY_STUDENT_NAME", name)
            putString("KEY_STUDENT_ID", id)
            putString("KEY_DEPARTMENT", dept)
            putString("KEY_YEAR", year)
            putString("KEY_EMAIL", email)
            apply()
        }

        val appPrefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        appPrefs.edit().putString("KEY_STUDENT_NAME", name).apply()

        tvWelcomeBanner.text = "Welcome back, $name!"
        Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}