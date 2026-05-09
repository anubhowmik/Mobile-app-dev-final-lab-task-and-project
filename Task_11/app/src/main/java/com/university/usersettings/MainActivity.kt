package com.university.usersettings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {

    private lateinit var etStudentName: EditText
    private lateinit var rgTheme: RadioGroup
    private lateinit var rbLight: RadioButton
    private lateinit var rbDark: RadioButton
    private lateinit var rbSystem: RadioButton
    private lateinit var switchNotif: SwitchCompat
    private lateinit var spinnerLanguage: Spinner
    private lateinit var seekBarFont: SeekBar
    private lateinit var tvFontSizeValue: TextView
    private lateinit var btnSave: MaterialButton
    private lateinit var btnReset: MaterialButton
    private lateinit var btnViewSaved: MaterialButton
    private lateinit var fabProfile: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        etStudentName = findViewById(R.id.etStudentName)
        rgTheme = findViewById(R.id.rgTheme)
        rbLight = findViewById(R.id.rbLight)
        rbDark = findViewById(R.id.rbDark)
        rbSystem = findViewById(R.id.rbSystem)
        switchNotif = findViewById(R.id.switchNotif)
        spinnerLanguage = findViewById(R.id.spinnerLanguage)
        seekBarFont = findViewById(R.id.seekBarFont)
        tvFontSizeValue = findViewById(R.id.tvFontSizeValue)
        btnSave = findViewById(R.id.btnSave)
        btnReset = findViewById(R.id.btnReset)
        btnViewSaved = findViewById(R.id.btnViewSaved)
        fabProfile = findViewById(R.id.fabProfile)

        val langAdapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item)
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguage.adapter = langAdapter

        seekBarFont.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                tvFontSizeValue.text = "Font Size: ${progress + 12}sp"
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        btnSave.setOnClickListener { saveSettings() }
        btnReset.setOnClickListener { resetPreferences() }

        btnViewSaved.setOnClickListener {
            startActivity(Intent(this, SettingsViewActivity::class.java))
        }

        fabProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        restoreSettings()
    }

    private fun saveSettings() {
        val studentName = etStudentName.text.toString().trim()

        if (studentName.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        val selectedTheme = when (rgTheme.checkedRadioButtonId) {
            R.id.rbDark   -> "dark"
            R.id.rbSystem -> "system"
            else          -> "light"
        }

        val selectedLang = spinnerLanguage.selectedItem.toString()

        with(prefs.edit()) {
            putString("KEY_THEME", selectedTheme)
            putBoolean("KEY_NOTIFICATIONS", switchNotif.isChecked)
            putString("KEY_LANGUAGE", selectedLang)
            putInt("KEY_FONT_SIZE", seekBarFont.progress + 12)
            putString("KEY_STUDENT_NAME", etStudentName.text.toString().trim())
            putLong("KEY_LAST_SAVED", System.currentTimeMillis())
            apply()
        }

        Toast.makeText(this, "Settings saved successfully!", Toast.LENGTH_SHORT).show()
    }

    private fun resetPreferences(){
        val prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        etStudentName.setText("")
        rbLight.isChecked = true
        switchNotif.isChecked = true
        seekBarFont.progress = 4
        spinnerLanguage.setSelection(0)
        tvFontSizeValue.text = "Font Size: 16sp"

        Toast.makeText(this, "Settings reset to default", Toast.LENGTH_SHORT).show()
    }

    private fun restoreSettings() {
        val prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        when (prefs.getString("KEY_THEME", "light")) {
            "light"  -> rbLight.isChecked  = true
            "dark"   -> rbDark.isChecked   = true
            "system" -> rbSystem.isChecked = true
        }

        switchNotif.isChecked = prefs.getBoolean("KEY_NOTIFICATIONS", true)

        val languages = resources.getStringArray(R.array.languages)
        val savedLang = prefs.getString("KEY_LANGUAGE", "English") ?: "English"
        val langIndex = languages.indexOf(savedLang).coerceAtLeast(0)
        spinnerLanguage.setSelection(langIndex)

        val savedFont = prefs.getInt("KEY_FONT_SIZE", 16)
        seekBarFont.progress = savedFont - 12
        tvFontSizeValue.text = "Font Size: ${savedFont}sp"

        etStudentName.setText(prefs.getString("KEY_STUDENT_NAME", ""))
    }
}