package com.university.usersettings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsViewActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings_view)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Saved Settings"

        val tvLastSaved : TextView = findViewById(R.id.tvLastSaved)
        val tvNoSettings : TextView = findViewById(R.id.tvNoSettings)
        val tvTheme : TextView = findViewById(R.id.tvTheme)
        val tvNotif : TextView = findViewById(R.id.tvNotif)
        val tvLanguage : TextView = findViewById(R.id.tvLanguage)
        val tvFontSize : TextView = findViewById(R.id.tvFontSize)
        val tvStudentName : TextView = findViewById(R.id.tvStudentName)
        val btnEdit : MaterialButton = findViewById(R.id.btnEdit)

        val prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val lastSavedTime = prefs.getLong("KEY_LAST_SAVED", 0L)

        if(lastSavedTime == 0L) {
            tvNoSettings.visibility = View.VISIBLE
            tvLastSaved.text = "Last Saved: Never"
        } else {
            tvNoSettings.visibility = View.GONE

            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            val formattedDate = sdf.format(Date(lastSavedTime))
            tvLastSaved.text = "Last Saved: $formattedDate"

            val theme = prefs.getString("KEY_THEME", "light") ?: "light"
            tvTheme.text = theme.replaceFirstChar { it.uppercase() }

            val notif = prefs.getBoolean("KEY_NOTIFICATIONS", true)
            tvNotif.text = if (notif) "Enabled" else "Disabled"

            tvLanguage.text  = prefs.getString("KEY_LANGUAGE", "English") ?: "English"
            tvFontSize.text  = "${prefs.getInt("KEY_FONT_SIZE", 16)}sp"
            tvStudentName.text = prefs.getString("KEY_STUDENT_NAME", "—") ?: "—"
        }

        btnEdit.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}