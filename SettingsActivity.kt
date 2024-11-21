package com.example.settingspagetest

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    private lateinit var dark_mode_switch : Switch
    private lateinit var unit_imperial : RadioButton
    private lateinit var unit_metric : RadioButton
    private lateinit var unit_choice : RadioGroup
    private lateinit var unit_preview : TextView

    private var isDark : Boolean = false
    private var isMetric : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dark_mode_switch = findViewById(R.id.dark_mode_switch)
        unit_imperial = findViewById(R.id.unit_imperial)
        unit_metric = findViewById(R.id.unit_metric)
        unit_choice = findViewById(R.id.unit_choice)
        unit_preview = findViewById(R.id.unit_preview)

        // The listener for if dark mode is used or not.
        // Change the base part of values/themes.xml or night\theme.xml to change the themes here.
        // THANK YOU SO MUCH Abhishek Pathak!
        // https://medium.com/@myofficework000/implementing-light-to-dark-and-vice-versa-theme-switch-in-android-16f2916b761f
        dark_mode_switch.setOnCheckedChangeListener {_, isChecked ->
            if (isChecked) isDark = true
            else isDark = false
            // This part does not allow me to set isDark in it - Leo.
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            setPreferences()
        }

        // The listener for if the unit choice is changed.
        unit_choice.setOnCheckedChangeListener { _, _ ->
            if (unit_imperial.isChecked) {
                unit_preview.text = "50 Farenheit\n30 mph"
                isMetric = false
                // Change units to imperial
            } else if (unit_metric.isChecked) {
                unit_preview.text = "10 Celcius\n48.280 km/h"
                isMetric = true
                // Change units to metric
            }
            setPreferences()
        }

        // This has to be here, otherwise the changes won't properly register.
        getPreferences()
    }

    // Sets the preferences each time they are changed.
    fun setPreferences() {
        var pref : SharedPreferences =
            this.getSharedPreferences(this.packageName+ "_preferences", Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = pref.edit()
        editor.putBoolean(PREFERENCE_DARK, isDark)
        editor.putBoolean(PREFERENCE_UNIT, isMetric)
        editor.commit()
    }

    // Gets the preferences from the previous session and changes the UI
    // elements accordingly.
    fun getPreferences() {
        var pref : SharedPreferences =
            this.getSharedPreferences(this.packageName+ "_preferences", Context.MODE_PRIVATE)

        isDark = pref.getBoolean(PREFERENCE_DARK, false)
        isMetric = pref.getBoolean(PREFERENCE_UNIT, false)

        dark_mode_switch.isChecked = isDark
        if (isMetric) unit_metric.isChecked = true
        else unit_imperial.isChecked = true
    }

    companion object {
        private const val PREFERENCE_DARK : String = "dark"
        private const val PREFERENCE_UNIT : String = "unit"
    }
}